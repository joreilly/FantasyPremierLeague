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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrain
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import kotlin.math.max
import kotlin.math.roundToInt
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
/**
 * A layout that allows you to place two different pieces of content: start and end on the screen,
 * where the arrangement, sizes and separation behaviour is controlled by [TwoPaneState].
 *
 * The default implementations of [TwoPaneState] are fold and hinges aware, meaning that the two
 * pane will adopt its layout to properly separate [start] and [end] panes so they don't interfere
 * with hardware hinges (vertical or horizontal), or respect folds when needed (for example,
 * when foldable is half-folded (90-degree fold AKA tabletop) the split will become on the bend)
 *
 * @param start a start content of the layout, a left-most in LTR, a right-most in RTL and top-most
 * in a vertical split if [TwoPaneState.isHorizontalSplit] is true
 * @param end an end content of the layout, a right-most in the LRT, a left-most in the RTL and the
 * bottom-most in a vertical split if [TwoPaneState.isHorizontalSplit] is true
 * @param state state of the two pane that controls the arrangement of the layout
 * @param modifier an optional modifier for the layout
 */
@Composable
fun TwoPane(
    start: @Composable () -> Unit,
    end: @Composable () -> Unit,
    state: TwoPaneState,
    modifier: Modifier = Modifier
) {
    val coords = remember { mutableStateOf<LayoutCoordinates?>(null) }
    Layout(
        modifier = modifier
            .wrapContentSize(Alignment.TopStart)
            // temporary onGloballyPositioned solution while waiting for a proper one
            .onGloballyPositioned { coords.value = it },
        content = {
            Box(Modifier.layoutId("list")) {
                start()
            }
            Box(Modifier.layoutId("detail")) {
                end()
            }
        }
    ) { measurable, constraints ->
        // TODO: test orientation change of fold
        val listMeasurable = measurable.find { it.layoutId == "list" }!!
        val detailMeasurable = measurable.find { it.layoutId == "detail" }!!
        // This measuring should be done by converting window-wide coordinates and hinges to
        // the local ones. It is not possible right now without onGloballyPositioned
        val splitArea = coords.value?.let {
            val adjustedTL = it.windowToLocal(state.splitAreaInWindow.topLeft)
            val adjustedBR = it.windowToLocal(state.splitAreaInWindow.bottomRight)
            Rect(adjustedTL, adjustedBR)
        } ?: state.splitAreaInWindow
        val splitLeft = constraints.constrainWidth(splitArea.left.roundToInt())
        val splitRight = constraints.constrainWidth(splitArea.right.roundToInt())
        val splitTop = constraints.constrainHeight(splitArea.top.roundToInt())
        val splitBottom = constraints.constrainHeight(splitArea.bottom.roundToInt())
        val listConstraints =
            if (state.isHorizontalSplit) {
                constraints.copy(minWidth = splitLeft, maxWidth = splitLeft)
            } else {
                constraints.copy(minHeight = splitTop, maxHeight = splitTop)
            }
        val detailConstraints =
            Constraints.fixed(
                constraints.maxWidth - if (state.isHorizontalSplit) splitRight else 0,
                constraints.maxHeight - if (state.isHorizontalSplit) 0 else splitBottom
            )
        val listPlaceable = listMeasurable.measure(constraints.constrain(listConstraints))
        val detailsPlaceable = detailMeasurable.measure(constraints.constrain(detailConstraints))
        val hingeWidth = if (state.isHorizontalSplit) splitArea.width.roundToInt() else 0
        val hingeHeight = if (state.isHorizontalSplit) 0 else splitArea.height.roundToInt()
        val totalWidth = if (state.isHorizontalSplit) {
            listPlaceable.width + detailsPlaceable.width + hingeWidth
        } else {
            max(listPlaceable.width, detailsPlaceable.width)
        }
        val totalHeight = if (state.isHorizontalSplit) {
            max(listPlaceable.height, detailsPlaceable.height)
        } else {
            listPlaceable.height + detailsPlaceable.height + hingeHeight
        }
        layout(totalWidth, totalHeight) {
            listPlaceable.placeRelative(0, 0)
            val detailOffsetX =
                if (state.isHorizontalSplit) listPlaceable.width + hingeWidth else 0
            val detailOffsetY =
                if (state.isHorizontalSplit) 0 else listPlaceable.height + hingeHeight
            detailsPlaceable.placeRelative(detailOffsetX, detailOffsetY)
        }
    }
}
/**
 * A helper modifier that tracks any interaction happening on the element.
 *
 * This is usually helpful to understand which side of the [TwoPane] was interacted last so when the
 * layout is switch to a single pane the most appropriate UI is shown.
 *
 * @param onInteracted a callback to be invoked when the modifier element is interacted
 */
@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.userInteractionNotification(onInteracted: () -> Unit): Modifier {
    return pointerInput(onInteracted) {
        val currentContext = currentCoroutineContext()
        awaitPointerEventScope {
            while (currentContext.isActive) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                // if user taps (down) or scrolls - consider it an interaction signal
                if (
                    event.type == PointerEventType.Press || event.type == PointerEventType.Scroll
                ) {
                    onInteracted.invoke()
                }
            }
        }
    }
}
/**
 * A state of the [TwoPane] component, that is responsible for the meta-data corresponding to the
 * arrangement of the `start` and `end` panes of the layout.
 */
@Stable
interface TwoPaneState {
    /**
     * Whether the split should be done vertically or horizontally
     */
    val isHorizontalSplit: Boolean
    /**
     * The ares representing a "split" within **window** bounds. Split area is the area that is
     * nether a `start` pane or an `end` pane, but a separation between those two. In case width
     * or height is 0 - it means that the area itself is a 0 width/height, but the place within
     * the window is still defined.
     */
    val splitAreaInWindow: Rect
}
