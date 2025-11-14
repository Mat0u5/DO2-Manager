# Contributing to DO2-Manager

## Prerequisites

- **Java 21** (required for Fabric Loom 1.8)
- Docker and Docker Compose (for testing)

## Setup

```bash
git clone https://github.com/Mat0u5/DO2-Manager.git
cd DO2-Manager

# Ensure you're using Java 21
java -version

# Build the mod
./gradlew build
```

## Testing with Docker

We provide a Docker test environment with a real Minecraft server.

### Commands

| Command | Description |
|---------|-------------|
| `./test-docker.sh fresh` | Reset everything, test fresh installation |
| `./test-docker.sh start` | Start test environment (default) |
| `./test-docker.sh logs` | View server logs |
| `./test-docker.sh stop` | Stop test environment |

### Testing Database Changes

```bash
# Always test fresh installations for database changes
./test-docker.sh fresh

# Watch for these success logs:
# "Initializing database"
# "Connection established successfully"
# "Database tables created successfully. Version set to v.1.1.1"
# "Database initialized"
# "Initializing DO2-Manager"
```
