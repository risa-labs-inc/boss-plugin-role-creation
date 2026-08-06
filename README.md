# BOSS Role Creation

Define roles and permissions, in the right sidebar as **Admin: Create Roles**.

The authoring half of BOSS's RBAC: this plugin creates roles and permissions and wires them
together. Assigning them to people is the job of its sibling, [Admin
Roles](https://github.com/risa-labs-inc/boss-plugin-admin-role-management), which sits directly
above it in the same slot.

Roles and permissions are rows in a table, not enum values, so they can be added at runtime
without a release.

## What it does

- **Lists every role and every permission** side by side, loaded together when the panel opens.
- **Create a role** (name plus optional description) or **create a permission**, each validated
  in the panel before the call goes out.
- **Grant a permission to a role** and revoke it again. Expand a role to see what it currently
  holds.
- **Delete roles and permissions**, with system entries protected: the action needs both
  `!role.isSystem` and an admin session.
- **Provenance badges** show which plugin defined a permission, read best-effort from the
  `plugin_permissions` table. If that read fails the badge just does not render.

## MCP tools

| Tool | Purpose |
|---|---|
| `permissions_list` | All permissions, with their system flag |
| `role_permissions` | Permissions granted to a role |
| `role_create` | Create a role |
| `role_delete` | Delete a role |
| `permission_create` | Create a permission |
| `role_grant_permission` | Grant a permission to a role |
| `role_revoke_permission` | Revoke a permission from a role |

## Permissions

Manifest `requiredPermissions` is `["role.read", "role.create"]`.

Per-tool gates are mapped deliberately onto what the server RPC will actually authorize, so a
tool that is visible is never guaranteed to be rejected: reads need `role.read`, `role_delete`
needs `role.delete`, grant and revoke need `role.update`, and everything else needs
`role.create`.

## Requirements

- BOSS >= 9.2.20, boss-plugin-api >= 1.0.20
- `roleManagementProvider` is load-bearing. Without it the plugin registers a degraded panel
  that explains the situation instead of failing.
- `authDataProvider` for the admin checks; `supabaseDataProvider` is optional and only powers
  the provenance badges, so it may be absent on older hosts.
- No external binaries.

## Build

```bash
./gradlew buildPluginJar
cp build/libs/boss-plugin-role-creation-*.jar ~/.boss/plugins/
```

See [AGENTS.md](AGENTS.md) for architecture and conventions.

## License

Proprietary - Risa Labs Inc.
