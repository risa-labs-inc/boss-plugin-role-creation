package ai.rever.boss.plugin.dynamic.rolecreation

import ai.rever.boss.plugin.api.McpToolDefinition
import ai.rever.boss.plugin.api.McpToolHandler
import ai.rever.boss.plugin.api.McpToolProvider
import ai.rever.boss.plugin.api.McpToolResult
import ai.rever.boss.plugin.api.RoleManagementProvider

/**
 * MCP tools contributed by the Role Creation plugin: list permissions, inspect a
 * role's permissions, and create/delete roles + grant/revoke permissions. Loads
 * only where role management is available. Registered in
 * [RoleCreationDynamicPlugin.register]; removed automatically on disable/unload.
 */
internal class RoleCreationMcpToolProvider(
    override val providerId: String,
    private val roles: RoleManagementProvider,
) : McpToolProvider {

    override fun tools(): List<McpToolDefinition> = listOf(
        McpToolDefinition(
            name = "permissions_list",
            description = "List all permissions (name, system flag).",
            handler = McpToolHandler {
                roles.getAllPermissions().fold(
                    onSuccess = { perms ->
                        if (perms.isEmpty()) McpToolResult("No permissions.")
                        else McpToolResult(perms.joinToString("\n") { "${it.name}${if (it.isSystem) "\t[system]" else ""}" })
                    },
                    onFailure = { McpToolResult("Failed: ${it.message}", isError = true) },
                )
            },
        ),
        McpToolDefinition(
            name = "role_permissions",
            description = "List the permissions granted to a role.",
            inputSchema = ROLE_SCHEMA,
            handler = McpToolHandler { args ->
                val role = args.string("role")
                    ?: return@McpToolHandler McpToolResult("Missing required argument: role", isError = true)
                roles.getRolePermissions(role).fold(
                    onSuccess = { rp -> McpToolResult("${rp.roleName}: ${rp.permissions.joinToString(", ").ifBlank { "(none)" }}") },
                    onFailure = { McpToolResult("Failed: ${it.message}", isError = true) },
                )
            },
        ),
        McpToolDefinition(
            name = "role_create",
            description = "Create a new role (name + optional description).",
            inputSchema = CREATE_SCHEMA,
            readOnly = false,
            handler = McpToolHandler { args ->
                val name = args.string("name")
                    ?: return@McpToolHandler McpToolResult("Missing required argument: name", isError = true)
                roles.validateRoleName(name)?.let { return@McpToolHandler McpToolResult("Invalid role name: $it", isError = true) }
                roles.createRole(name, args.string("description")).fold(
                    onSuccess = { McpToolResult("Created role ${it.name}.") },
                    onFailure = { McpToolResult("Failed: ${it.message}", isError = true) },
                )
            },
        ),
        McpToolDefinition(
            name = "role_delete",
            description = "Delete a role by name.",
            inputSchema = ROLE_SCHEMA,
            readOnly = false,
            handler = McpToolHandler { args ->
                val role = args.string("role")
                    ?: return@McpToolHandler McpToolResult("Missing required argument: role", isError = true)
                roles.deleteRole(role).fold(
                    onSuccess = { McpToolResult("Deleted role $role.") },
                    onFailure = { McpToolResult("Failed: ${it.message}", isError = true) },
                )
            },
        ),
        McpToolDefinition(
            name = "permission_create",
            description = "Create a new permission (name + optional description).",
            inputSchema = CREATE_SCHEMA,
            readOnly = false,
            handler = McpToolHandler { args ->
                val name = args.string("name")
                    ?: return@McpToolHandler McpToolResult("Missing required argument: name", isError = true)
                roles.validatePermissionName(name)?.let { return@McpToolHandler McpToolResult("Invalid permission name: $it", isError = true) }
                roles.createPermission(name, args.string("description")).fold(
                    onSuccess = { McpToolResult("Created permission ${it.name}.") },
                    onFailure = { McpToolResult("Failed: ${it.message}", isError = true) },
                )
            },
        ),
        McpToolDefinition(
            name = "role_grant_permission",
            description = "Grant a permission to a role.",
            inputSchema = ROLE_PERM_SCHEMA,
            readOnly = false,
            handler = McpToolHandler { args -> grant(args) { r, p -> roles.assignPermissionToRole(r, p) } },
        ),
        McpToolDefinition(
            name = "role_revoke_permission",
            description = "Revoke a permission from a role.",
            inputSchema = ROLE_PERM_SCHEMA,
            readOnly = false,
            handler = McpToolHandler { args -> grant(args) { r, p -> roles.removePermissionFromRole(r, p) } },
        ),
    ).onEach { it.requiredPermissions = permissionsFor(it.name) }

    // RBAC gate (admins bypass), aligned with the SERVER RPC authorization so a
    // visible tool never gets a guaranteed server rejection: reads → role.read;
    // create → role.create; delete → role.delete; grant/revoke → role.update.
    private fun permissionsFor(tool: String): List<String> = when (tool) {
        "permissions_list", "role_permissions" -> listOf("role.read")
        "role_delete" -> listOf("role.delete")
        "role_grant_permission", "role_revoke_permission" -> listOf("role.update")
        else -> listOf("role.create")
    }

    private suspend fun grant(
        args: ai.rever.boss.plugin.api.McpToolArgs,
        op: suspend (String, String) -> Result<Unit>,
    ): McpToolResult {
        val role = args.string("role")
            ?: return McpToolResult("Missing required argument: role", isError = true)
        val permission = args.string("permission")
            ?: return McpToolResult("Missing required argument: permission", isError = true)
        return op(role, permission).fold(
            onSuccess = { McpToolResult("OK") },
            onFailure = { McpToolResult("Failed: ${it.message}", isError = true) },
        )
    }

    private companion object {
        const val ROLE_SCHEMA =
            """{"type":"object","properties":{"role":{"type":"string","description":"Role name."}},"required":["role"]}"""
        const val CREATE_SCHEMA =
            """{"type":"object","properties":{"name":{"type":"string"},"description":{"type":"string"}},"required":["name"]}"""
        const val ROLE_PERM_SCHEMA =
            """{"type":"object","properties":{"role":{"type":"string","description":"Role name."},"permission":{"type":"string","description":"Permission name."}},"required":["role","permission"]}"""
    }
}
