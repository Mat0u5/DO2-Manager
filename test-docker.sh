#!/bin/bash

set -e

# Check if mod jar exists
check_mod_jar() {
    if [ ! -f "./build/libs"/*.jar ]; then
        echo "Mod jar not found, building..."
        ./gradlew build
        if [ $? -ne 0 ]; then
            echo "Build failed!"
            exit 1
        fi
    else
        echo "Mod jar found: $(ls ./build/libs/*.jar)"
    fi
}

# Start the test environment
start_test() {
    echo "Starting test environment..."
    check_mod_jar
    docker compose up -d
    echo "Server starting... (may take a few minutes on first run)"
}

# View logs
view_logs() {
    echo "Viewing logs (Ctrl+C to exit)..."
    docker compose logs -f minecraft-server
}

# Test fresh installation
fresh_test() {
    echo "This will destroy all existing server data!"
    read -p "Are you sure? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo "Stopping server..."
        docker compose down
        echo "Removing old data..."
        docker volume rm do2-manager_minecraft-data 2>/dev/null || true
        echo "Starting fresh server..."
        start_test
        echo "Fresh installation started!"
        sleep 2
        view_logs
    else
        echo "Cancelled."
    fi
}

# Stop the environment
stop_test() {
    echo "Stopping test environment..."
    docker compose down
    echo "Stopped."
}

# Simple command handling
case "${1:-start}" in
    start)
        start_test
        ;;
    logs)
        view_logs
        ;;
    fresh)
        fresh_test
        ;;
    stop)
        stop_test
        ;;
    *)
        echo "Usage: $0 [start|logs|fresh|stop]"
        echo "  start - Start the test environment (default)"
        echo "  logs  - View server logs"
        echo "  fresh - Reset and test fresh installation"
        echo "  stop  - Stop the test environment"
        exit 1
        ;;
esac
