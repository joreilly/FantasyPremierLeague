package dev.johnoreilly.common.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ObjCClass
import kotlinx.cinterop.getOriginalKotlinClass
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.error.InstanceCreationException
import org.koin.core.error.NoDefinitionFoundException
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.mp.KoinPlatform
import org.koin.viewmodel.defaultExtras
import org.koin.viewmodel.resolveViewModel
import kotlin.reflect.KClass

/**
 * This function allows retrieving any ViewModel from Swift Code with generics.
 * We only get [ObjCClass] type for the [vmClass], because the interop between Kotlin and Swift
 * code doesn't preserve the generic class, but we can retrieve the original KClass in Kotlin.
 */
@OptIn(BetaInteropApi::class, KoinInternalApi::class)
@Throws(
    IllegalStateException::class,
    NoDefinitionFoundException::class,
    InstanceCreationException::class,
    UnsupportedOperationException::class
)
fun koinResolveViewModel(
    vmClass: ObjCClass,
    owner: ViewModelStoreOwner,
    key: String?,
    extras: CreationExtras?,
    qualifier: Qualifier?,
    scope: Scope?,
    parameters: ParametersDefinition?,
): ViewModel {
    @Suppress("UNCHECKED_CAST")
    val vmClass = getOriginalKotlinClass(vmClass) as? KClass<ViewModel>
        ?: error("vmClass isn't a ViewModel type")

    return resolveViewModel(
        vmClass = vmClass,
        viewModelStore = owner.viewModelStore,
        key = key,
        extras = extras ?: defaultExtras(owner),
        qualifier = qualifier,
        scope = scope ?: KoinPlatform.getKoinOrNull()?.scopeRegistry?.rootScope
        ?: error("No Koin scope available"),
        parameters = parameters,
    )
}


@OptIn(BetaInteropApi::class)
@Throws(
    IllegalStateException::class,
    NoDefinitionFoundException::class,
    InstanceCreationException::class,
    UnsupportedOperationException::class
)
fun koinResolveViewModel(
    vmClass: ObjCClass,
    owner: ViewModelStoreOwner,
    key: String? = null,
    extras: CreationExtras? = null,
) = koinResolveViewModel(vmClass, owner, key, extras, null, null, null)
