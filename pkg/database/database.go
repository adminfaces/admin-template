package database

import (
	"database/sql"
	"fmt"
	"log"
	"os"
	"time"

	_ "github.com/go-sql-driver/mysql" // MySQL driver
)

var DB *sql.DB

// InitDB initializes the database connection
func InitDB() {
	dbUser := os.Getenv("MYSQL_USER")
	if dbUser == "" {
		dbUser = "myapp_user"
	}
	dbPassword := os.Getenv("MYSQL_PASSWORD")
	if dbPassword == "" {
		dbPassword = "myapp_password"
	}
	dbName := os.Getenv("MYSQL_DATABASE")
	if dbName == "" {
		dbName = "myapp_db"
	}
	dbHost := os.Getenv("DB_HOST") // Will be 'db' when running in Docker, 'localhost' for local dev
	if dbHost == "" {
		dbHost = "localhost" // Default to localhost if not set (e.g. local development without Docker)
	}

	dataSourceName := fmt.Sprintf("%s:%s@tcp(%s:3306)/%s?parseTime=true", dbUser, dbPassword, dbHost, dbName)

	var err error
	// It's important to retry connection for a few seconds, as the DB container might not be ready immediately
	for i := 0; i < 10; i++ {
		DB, err = sql.Open("mysql", dataSourceName)
		if err != nil {
			log.Printf("Error opening database connection (attempt %d/10): %v\n", i+1, err)
			time.Sleep(2 * time.Second)
			continue
		}
		err = DB.Ping()
		if err != nil {
			log.Printf("Error pinging database (attempt %d/10): %v\n", i+1, err)
			DB.Close() // Close the connection if ping fails
			time.Sleep(2 * time.Second)
			continue
		}
		break // Connection successful
	}

	if err != nil {
		log.Fatalf("Could not connect to the database after several retries: %v", err)
	}

	log.Println("Successfully connected to the database!")
}

// GetDB returns the database connection pool
func GetDB() *sql.DB {
	if DB == nil {
		// This case should ideally not happen if InitDB is called at startup
		log.Println("DB connection is nil. Attempting to re-initialize.")
		InitDB()
		if DB == nil {
			// If still nil after re-initialization, something is seriously wrong.
			log.Fatal("Failed to re-initialize DB connection.")
		}
	}
	err := DB.Ping()
	if err != nil {
		log.Printf("DB connection lost. Pinging failed: %v. Attempting to re-initialize.", err)
		DB.Close()
		InitDB() // Try to re-establish connection
		if DB == nil || DB.Ping() != nil {
			log.Fatal("Failed to re-establish DB connection after ping failure.")
		}
	}
	return DB
}
