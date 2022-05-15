/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.compose.material.window
import android.app.Activity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowMetrics
import androidx.window.layout.WindowMetricsCalculator
import kotlinx.coroutines.flow.collect
/**
 * Create and remember a [TwoPaneState] where the "start" pane will take a fraction
 * [startFractionSplit] of the available space, leaving the rest to the "end" side.
 *
 * **Note**: this is a fold-aware version of the [TwoPaneState], unless [respectFoldsAndHinges] is
 * specified as `false`. It means that in cases where the hardware hinge is present and in other
 * "separating" circumstances (such as 90-degree fold AKA tabletop mode) the [startFractionSplit]
 * and [isHorizontal] will be ignored.
 *
 * @param startFractionSplit the amount of percentage of the total layout to take for `start` pane
 * @param isHorizontal whether the split should be vertical or horizontal
 * @param respectFoldsAndHinges whether to make a returned [TwoPaneState] to be fold aware.
 */
@Composable
fun Activity.rememberTwoPaneState(
    startFractionSplit: Float,
    isHorizontal: Boolean = true,
    respectFoldsAndHinges: Boolean = true
): TwoPaneState {
    val metrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this)
    val twoPaneState = remember(isHorizontal, startFractionSplit, metrics) {
        DefaultTwoPaneState(isHorizontal, startFractionSplit, metrics)
    }
    return if (respectFoldsAndHinges) {
        rememberFoldAwareTwoPaneState(nonFoldableFallback = twoPaneState)
    } else {
        twoPaneState
    }
}
/**
 * Create and remember a [TwoPaneState] where the "start" pane will take a fixed amount of
 * [startFixedSize] of [Dp] specificed, leaving the rest for the "end" side.
 *
 * * **Note**: this is a fold-aware version of the [TwoPaneState], unless [respectFoldsAndHinges] is
 * specified as `false`. It means that in cases where the hardware hinge is present and in other
 * "separating" circumstances (such as 90-degree fold AKA tabletop mode) the [startFixedSize] and
 * [isHorizontal] will be ignored.
 *
 * @param startFixedSize the fixed size in [Dp] to take for a `start` pane
 * @param isHorizontal whether the split should be vertical or horizontal
 * @param respectFoldsAndHinges whether to make a returned [TwoPaneState] to be fold aware.
 */
@Composable
fun Activity.rememberTwoPaneState(
    startFixedSize: Dp,
    isHorizontal: Boolean = true,
    respectFoldsAndHinges: Boolean = true
): TwoPaneState {
    val metrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this)
    val density = LocalDensity.current
    val twoPaneState = remember(isHorizontal, startFixedSize, metrics) {
        val minSizePx = with(density) { startFixedSize.toPx() }
        DefaultMinSizeTwoPaneState(isHorizontal, minSizePx, metrics)
    }
    return if (respectFoldsAndHinges) {
        rememberFoldAwareTwoPaneState(twoPaneState)
    } else {
        twoPaneState
    }
}
private class DefaultTwoPaneState(
    override val isHorizontalSplit: Boolean,
    val startFraction: Float,
    val metrics: WindowMetrics,
) : TwoPaneState {
    override val splitAreaInWindow: Rect
        get() = metrics.bounds.toComposeRect().let { metrics ->
            if (isHorizontalSplit) {
                metrics.copy(
                    left = metrics.width * startFraction,
                    right = metrics.width * startFraction,
                )
            } else {
                metrics.copy(
                    top = metrics.height * startFraction,
                    bottom = metrics.height * startFraction,
                )
            }
        }
}
private class DefaultMinSizeTwoPaneState(
    override val isHorizontalSplit: Boolean,
    val listMinSizePx: Float,
    val metrics: WindowMetrics,
) : TwoPaneState {
    override val splitAreaInWindow: Rect
        get() = metrics.bounds.toComposeRect().let { metrics ->
            if (isHorizontalSplit) {
                metrics.copy(left = listMinSizePx, right = listMinSizePx)
            } else {
                metrics.copy(top = listMinSizePx, bottom = listMinSizePx)
            }
        }
}
private class FoldingTwoPaneState(
    val fallback: TwoPaneState,
    val animationSpec: AnimationSpec<Rect>
) : TwoPaneState {
    private val foldingFeature = mutableStateOf<FoldingFeature?>(null)
    suspend fun updateFoldingFeature(foldingFeature: FoldingFeature) {
        animatableRect.animateTo(calculateRect(foldingFeature), animationSpec)
    }
    override val isHorizontalSplit: Boolean
        get() {
            val currentFold = foldingFeature.value
            return if (currentFold == null) {
                fallback.isHorizontalSplit
            } else {
                // the split is horizontal if the hinge is vertical :)
                currentFold.orientation == FoldingFeature.Orientation.VERTICAL
            }
        }
    fun calculateRect(foldingFeature: FoldingFeature?): Rect {
        val currentFold = foldingFeature
        return if (currentFold != null && currentFold.isSeparating) {
            currentFold.bounds.toComposeRect()
        } else {
            fallback.splitAreaInWindow
        }
    }
    val animatableRect = Animatable(calculateRect(null), Rect.VectorConverter)
    override val splitAreaInWindow: Rect
        get() = animatableRect.value
}
/**
 * Construct the best possible list-detail split for devices with folds and hinges, falling back
 * to the [nonFoldableFallback] for the devices with no hinges folds or other obstructing
 * features
 */
@Composable
private fun Activity.rememberFoldAwareTwoPaneState(
    nonFoldableFallback: TwoPaneState,
    animationSpec: AnimationSpec<Rect> = tween(200)
): TwoPaneState {
    val windowInfo = WindowInfoTracker.getOrCreate(this).windowLayoutInfo(this)
    val twoPaneState = remember(nonFoldableFallback, animationSpec) {
        FoldingTwoPaneState(nonFoldableFallback, animationSpec)
    }
    LaunchedEffect(windowInfo) {
        windowInfo.collect { info ->
            info.displayFeatures.forEach { feature ->
                if (feature is FoldingFeature) twoPaneState.updateFoldingFeature(feature)
            }
        }
    }
    return twoPaneState
}
