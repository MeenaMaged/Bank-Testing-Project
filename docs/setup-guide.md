# Setup Guide

## Prerequisites

### Required Software
1. **Java Development Kit (JDK) 17 or higher**
   - Download: https://adoptium.net/
   - Verify: `java -version`

2. **Apache Maven 3.6+**
   - Download: https://maven.apache.org/download.cgi
   - Verify: `mvn -version`

3. **Google Chrome** (for Selenium tests)
   - Download: https://www.google.com/chrome/
   - ChromeDriver is managed automatically by WebDriverManager

4. **IDE (Recommended)**
   - IntelliJ IDEA: https://www.jetbrains.com/idea/
   - Eclipse: https://www.eclipse.org/downloads/

## Installation Steps

### 1. Clone/Extract Project
```bash
# If using Git
git clone <repository-url>

# Or extract the ZIP file
unzip "Bank Testing project.zip"
```

### 2. Navigate to Project Directory
```bash
cd "Bank Testing project/Bank Testing project"
```

### 3. Install Dependencies
```bash
mvn clean install -DskipTests
```

### 4. Verify Setup
```bash
mvn compile
```

## Running the Application

### Launch GUI
```bash
mvn exec:java -Dexec.mainClass="com.banking.gui.BankingGUI"
```

Or run `BankingGUI.java` directly from your IDE.

## Running Tests

### Run All Tests
```bash
mvn test
```

### Run Specific Test Categories

**Black-Box Tests:**
```bash
mvn test -Dtest=AccountBlackBoxTest
```

**White-Box Tests:**
```bash
mvn test -Dtest=TransactionServiceWhiteBoxTest
```

**State-Based Tests:**
```bash
mvn test -Dtest=AccountStateTest
```

**UI Tests:**
```bash
mvn test -Dtest=GUIComponentTest
```

**Selenium Tests:**
```bash
mvn test -Dtest=BankingSeleniumTest
```

**Integration Tests:**
```bash
mvn test -Dtest=IntegrationTest
```

**TDD Tests:**
```bash
mvn test -Dtest=CreditScoreGreenPhaseTest
```

### Generate Code Coverage Report
```bash
mvn test jacoco:report
```
Report location: `target/site/jacoco/index.html`

## Project Structure

```
Bank Testing project/
├── src/
│   ├── main/java/com/banking/
│   │   ├── controllers/     # Controller layer
│   │   ├── dao/             # Data access layer
│   │   ├── entities/        # Domain entities
│   │   ├── gui/             # Swing GUI
│   │   └── services/        # Business logic
│   └── test/
│       ├── java/com/banking/
│       │   ├── blackbox/    # Black-box tests
│       │   ├── whitebox/    # White-box tests
│       │   ├── state/       # State-based tests
│       │   ├── ui/          # GUI unit tests
│       │   ├── selenium/    # Browser automation
│       │   ├── integration/ # Integration tests
│       │   ├── tdd/         # TDD tests
│       │   ├── dao/         # DAO tests
│       │   └── services/    # Service tests
│       └── resources/
│           └── banking-test-page.html  # Selenium test page
├── docs/                    # Documentation
├── pom.xml                  # Maven configuration
└── README.md                # Project overview
```

## Troubleshooting

### Maven Build Fails
```bash
# Clean and rebuild
mvn clean install -U
```

### Selenium Tests Fail
1. Ensure Chrome is installed
2. Tests run in headless mode by default
3. To run with visible browser, remove `--headless` from ChromeOptions

### Java Version Issues
Ensure JAVA_HOME points to JDK 17+:
```bash
echo $JAVA_HOME   # Linux/Mac
echo %JAVA_HOME%  # Windows
```

### Port Already in Use
If running GUI fails, ensure no other instance is running.

## IDE Configuration

### IntelliJ IDEA
1. Open project folder
2. Import as Maven project
3. Wait for indexing to complete
4. Right-click test class → Run

### Eclipse
1. File → Import → Maven → Existing Maven Projects
2. Select project directory
3. Right-click project → Maven → Update Project
