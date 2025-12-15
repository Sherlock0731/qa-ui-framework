#!/bin/bash

# UI Test Automation Framework - Test Execution Script
# Usage: ./run-tests.sh [options]

set -e

# Default values
ENV="local"
BROWSER="chrome"
HEADLESS="false"
THREAD_COUNT="1"
TEST_GROUP=""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Help function
show_help() {
    echo "Usage: ./run-tests.sh [options]"
    echo ""
    echo "Options:"
    echo "  -e, --env ENV             Environment (local, ci) [default: local]"
    echo "  -b, --browser BROWSER     Browser (chrome, firefox, edge, safari, opera) [default: chrome]"
    echo "  -h, --headless            Run in headless mode"
    echo "  -t, --threads COUNT       Number of parallel threads [default: 1]"
    echo "  -g, --group GROUP         Test group (login, inventory, cart, checkout, navigation, smoke)"
    echo "  --allure                  Generate and open Allure report after tests"
    echo "  --help                    Show this help message"
    echo ""
    echo "Examples:"
    echo "  ./run-tests.sh                                    # Run all tests with defaults"
    echo "  ./run-tests.sh -b firefox -t 4                   # Run with Firefox, 4 threads"
    echo "  ./run-tests.sh -g login --headless               # Run login tests in headless mode"
    echo "  ./run-tests.sh -g smoke -t 2 --allure           # Run smoke tests and show report"
}

# Parse arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -e|--env)
            ENV="$2"
            shift 2
            ;;
        -b|--browser)
            BROWSER="$2"
            shift 2
            ;;
        -h|--headless)
            HEADLESS="true"
            shift
            ;;
        -t|--threads)
            THREAD_COUNT="$2"
            shift 2
            ;;
        -g|--group)
            TEST_GROUP="$2"
            shift 2
            ;;
        --allure)
            GENERATE_ALLURE="true"
            shift
            ;;
        --help)
            show_help
            exit 0
            ;;
        *)
            echo -e "${RED}Unknown option: $1${NC}"
            show_help
            exit 1
            ;;
    esac
done

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}UI Test Automation Framework${NC}"
echo -e "${GREEN}========================================${NC}"
echo -e "Environment:     ${YELLOW}${ENV}${NC}"
echo -e "Browser:         ${YELLOW}${BROWSER}${NC}"
echo -e "Headless:        ${YELLOW}${HEADLESS}${NC}"
echo -e "Threads:         ${YELLOW}${THREAD_COUNT}${NC}"
echo -e "Test Group:      ${YELLOW}${TEST_GROUP:-all}${NC}"
echo -e "${GREEN}========================================${NC}"

# Build Maven command
MVN_CMD="mvn clean test"
MVN_CMD="$MVN_CMD -Denv=${ENV}"
MVN_CMD="$MVN_CMD -Dbrowser=${BROWSER}"
MVN_CMD="$MVN_CMD -Dheadless=${HEADLESS}"
MVN_CMD="$MVN_CMD -Dthread.count=${THREAD_COUNT}"

if [ -n "$TEST_GROUP" ]; then
    MVN_CMD="$MVN_CMD -P${TEST_GROUP}"
fi

# Load environment variables if .env exists
if [ -f .env ]; then
    echo -e "${YELLOW}Loading environment variables from .env${NC}"
    export $(cat .env | grep -v '^#' | xargs)
fi

# Execute tests
echo -e "${GREEN}Starting test execution...${NC}"
eval $MVN_CMD

# Check test results
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Tests completed successfully${NC}"
    EXIT_CODE=0
else
    echo -e "${RED}✗ Tests failed${NC}"
    EXIT_CODE=1
fi

# Generate Allure report if requested
if [ "$GENERATE_ALLURE" = "true" ]; then
    echo -e "${GREEN}Generating Allure report...${NC}"
    mvn allure:serve
fi

exit $EXIT_CODE
