package ai.rever.boss.plugin.dynamic.rolecreation

import ai.rever.boss.plugin.api.DynamicPlugin
import ai.rever.boss.plugin.api.PluginContext
import ai.rever.boss.plugin.api.RoleManagementProvider

/**
 * Role Creation dynamic plugin - Loaded from external JAR.
 *
 * Create and configure custom roles and permissions.
 * Uses roleManagementProvider from PluginContext when available.
 */
class RoleCreationDynamicPlugin : DynamicPlugin {
    override val pluginId: String = "ai.rever.boss.plugin.dynamic.rolecreation"
    override val displayName: String = "Role Creation (Dynamic)"
    override val version: String = "1.0.4"
    override val description: String = "Create and configure custom roles and permissions"
    override val author: String = "Risa Labs"
    override val url: String = "https://github.com/risa-labs-inc/boss-plugin-role-creation"

    override fun register(context: PluginContext) {
        // Try to get roleManagementProvider via reflection for backwards compatibility with 1.0.3
        val roleManagementProvider = try {
            val method = context.javaClass.getMethod("getRoleManagementProvider")
            method.invoke(context) as? RoleManagementProvider
        } catch (_: Exception) {
            null
        }

        context.panelRegistry.registerPanel(RoleCreationInfo) { ctx, panelInfo ->
            RoleCreationComponent(
                ctx = ctx,
                panelInfo = panelInfo,
                roleManagementProvider = roleManagementProvider
            )
        }
    }
}
