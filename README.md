# Roostify

A schedule management system for work shifts 
using Spring Boot and React.

# Setup

Clone the repository using `git clone https://github.com/MarcoCaspani/roostify `

Create a new project and a new database in Neon (https://neon.tech/)

Follow the next steps: 1) Backend Application Setup and 2) Frontend Application Setup

---

## Backend Application Setup

### Prerequisites
- Java v17 or newer
- Maven (if on MacOS, you can install it via Homebrew: `brew install maven`)
- Node.js (v18 or newer)
- npm
- A Neon database (PostgreSQL)


### Configuration
1. Configure your database connection in `src/main/resources/application.properties` (create the file if it does not exist):
   ```properties
   spring.datasource.url=jdbc:postgresql://<your-neon-db-url>
   spring.datasource.username=<your-db-username>
   spring.datasource.password=<your-db-password>
   spring.jpa.hibernate.ddl-auto=update
   ```
2. Ensure your Neon database is accessible and credentials are correct.

### Build and Run
To build the backend application:
```bash
mvn clean compile
```
To run the backend application:
```bash
mvn spring-boot:run
```
The backend will start on `http://localhost:8080` by default.


---

## Frontend Application Setup

### Prerequisites
- Node.js (v18 or newer recommended)
- npm

### Install dependencies
```bash
cd roostify-frontend
npm install
```

### Run the frontend application
```bash
npm start
```
The frontend will start on `http://localhost:3000` by default.

---

# Features

Roostify helps managers create and manage work schedules.
These are the currently implemented features:

Schedule management
* Create a schedule automatically if it does not exist
* View saved schedules by year and week
* Delete a saved schedule

Shift management
* Add a shift to a specific day and select an employee
* Remove a specific shift with a confirmation dialog

Employees management
* Visualise current list of employees

Export
• Download schedules in customised XLSX format for compatibility
• Include extra work hours in the exported file

# Tech Stack Used

* Java
* Maven
* SpringBoot
* Neon Database (PostgreSQL)
* React (Typescript)
* TailwindCSS
* Postman application (to debug API calls)


---

# Other resources

## How frontend was created
To create the React app based on the typescript template

`npx create-react-app roostify-frontend --template cra-template-typescript`

## Installation of TailwindCSS
To install tailwindcss
`npm install -D tailwindcss@3.4.4 postcss autoprefixer`
`npx tailwindcss init -p`

This should create two files: tailwind.config.js, postcss.config.js

To verify installation
`npm list tailwindcss`

To reinstall TailwindCSS
`npm uninstall tailwindcss`

`npm install -D tailwindcss@3.4.4 postcss autoprefixer`
