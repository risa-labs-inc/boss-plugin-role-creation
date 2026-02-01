package ai.rever.boss.plugin.dynamic.rolecreation

import ai.rever.boss.plugin.api.DynamicPlugin
import ai.rever.boss.plugin.api.PluginContext

/**
 * Role Creation dynamic plugin - Loaded from external JAR.
 *
 * Create and configure custom roles
 */
class RoleCreationDynamicPlugin : DynamicPlugin {
    override val pluginId: String = "ai.rever.boss.plugin.dynamic.rolecreation"
    override val displayName: String = "Role Creation (Dynamic)"
    override val version: String = "1.0.0"
    override val description: String = "Create and configure custom roles"
    override val author: String = "Risa Labs"
    override val url: String = "https://github.com/risa-labs-inc/boss-plugin-role-creation"

    override fun register(context: PluginContext) {
        context.panelRegistry.registerPanel(RoleCreationInfo) { ctx, panelInfo ->
            RoleCreationComponent(ctx, panelInfo)
        }
    }
}
