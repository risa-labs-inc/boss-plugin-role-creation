package ai.rever.boss.plugin.dynamic.rolecreation

import ai.rever.boss.plugin.api.PanelComponentWithUI
import ai.rever.boss.plugin.api.PanelInfo
import ai.rever.boss.plugin.api.RoleManagementProvider
import ai.rever.boss.plugin.ui.BossTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy

/**
 * Role Creation panel component (Dynamic Plugin)
 *
 * Uses roleManagementProvider from PluginContext for role and permission operations.
 */
class RoleCreationComponent(
    ctx: ComponentContext,
    override val panelInfo: PanelInfo,
    private val roleManagementProvider: RoleManagementProvider?
) : PanelComponentWithUI, ComponentContext by ctx {

    private val viewModel: RoleCreationViewModel? = roleManagementProvider?.let {
        RoleCreationViewModel(it)
    }

    init {
        lifecycle.doOnDestroy {
            viewModel?.dispose()
        }
    }

    @Composable
    override fun Content() {
        BossTheme {
            if (viewModel != null) {
                RoleCreationContent(viewModel)
            } else {
                // Provider not available - show stub UI
                ProviderNotAvailableContent()
            }
        }
    }
}

@Composable
private fun ProviderNotAvailableContent() {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.AddCircle,
                contentDescription = "Role Creation",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colors.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Role Creation",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(8.dp),
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Provider Not Available",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colors.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Role management provider is required.\nPlease update to plugin-api 1.0.4 or later.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
