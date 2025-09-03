# Roostify

A project to create a schedule for work shifts


# Setup

# Run the application

To run the backend application use

> mvn spring-boot:run

# Tech Stack Used

Maven

Java

SpringBoot

Neon Database

React (Typescript)

Postman application (to debug API calls)

# Frontend app

## How to run

> cd roostify-frontend
> 
> npm start

## How frontend was created
To create the app based on the typescript template

> npx create-react-app roostify-frontend --template cra-template-typescript

### Install TailwindCSS
To install tailwindcss
>  npm install -D tailwindcss@3.4.4 postcss autoprefixer
>
> npx tailwindcss init -p

This should create two files: tailwind.config.js, postcss.config.js

To verify installation
>npm list tailwindcss



To reinstall TailwindCSS
> npm uninstall tailwindcss
> 
>  npm install -D tailwindcss@3.4.4 postcss autoprefixer