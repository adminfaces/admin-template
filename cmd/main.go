package main

import (
	"encoding/json"
	"log"
	"net/http"
	"strings" // Added for future use, like trimming whitespace from item names

	"github.com/example/go-backend/pkg/database" // Import the database package
)

// Item struct to model your data
type Item struct {
	ID   int    `json:"id"`
	Name string `json:"name"`
}

func healthCheckHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(map[string]string{"status": "ok"})
}

func itemsHandler(w http.ResponseWriter, r *http.Request) {
	db := database.GetDB() // Get DB connection from our package
	w.Header().Set("Content-Type", "application/json")

	switch r.Method {
	case http.MethodGet:
		rows, err := db.Query("SELECT id, name FROM items")
		if err != nil {
			http.Error(w, "Failed to fetch items: "+err.Error(), http.StatusInternalServerError)
			log.Printf("Error querying items: %v", err)
			return
		}
		defer rows.Close()

		var items []Item
		for rows.Next() {
			var item Item
			if err := rows.Scan(&item.ID, &item.Name); err != nil {
				http.Error(w, "Failed to scan item: "+err.Error(), http.StatusInternalServerError)
				log.Printf("Error scanning item: %v", err)
				return
			}
			items = append(items, item)
		}
		if err := rows.Err(); err != nil {
			http.Error(w, "Error iterating items: "+err.Error(), http.StatusInternalServerError)
			log.Printf("Error iterating items: %v", err)
			return
		}

		if len(items) == 0 {
			// Return empty array instead of null if no items
			json.NewEncoder(w).Encode([]Item{})
			return
		}
		json.NewEncoder(w).Encode(items)

	case http.MethodPost:
		var newItem Item
		decoder := json.NewDecoder(r.Body)
		decoder.DisallowUnknownFields() // Prevent unknown fields

		if err := decoder.Decode(&newItem); err != nil {
			http.Error(w, "Invalid request payload: "+err.Error(), http.StatusBadRequest)
			log.Printf("Error decoding item: %v", err)
			return
		}
		defer r.Body.Close()

		// Basic validation
		trimmedName := strings.TrimSpace(newItem.Name)
		if trimmedName == "" {
			http.Error(w, "Item name cannot be empty", http.StatusBadRequest)
			return
		}
		if len(trimmedName) > 255 {
			http.Error(w, "Item name is too long (max 255 chars)", http.StatusBadRequest)
			return
		}


		stmt, err := db.Prepare("INSERT INTO items(name) VALUES(?)")
		if err != nil {
			http.Error(w, "Failed to prepare statement: "+err.Error(), http.StatusInternalServerError)
			log.Printf("Error preparing statement: %v", err)
			return
		}
		defer stmt.Close()

		res, err := stmt.Exec(trimmedName)
		if err != nil {
			http.Error(w, "Failed to insert item: "+err.Error(), http.StatusInternalServerError)
			log.Printf("Error inserting item: %v", err)
			return
		}

		id, err := res.LastInsertId()
		if err != nil {
			http.Error(w, "Failed to retrieve last insert ID: "+err.Error(), http.StatusInternalServerError)
			log.Printf("Error getting last insert ID: %v", err)
			return
		}

		newItem.ID = int(id) // Assign the new ID to the item
		newItem.Name = trimmedName // Use the trimmed name

		w.WriteHeader(http.StatusCreated)
		json.NewEncoder(w).Encode(newItem)

	default:
		http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
	}
}

func main() {
	// Initialize database connection
	database.InitDB() // This will run when the application starts

	http.HandleFunc("/api/health", healthCheckHandler)
	http.HandleFunc("/api/items", itemsHandler) // Register the new items handler

	log.Println("Server starting on port 8080...")
	if err := http.ListenAndServe(":8080", nil); err != nil {
		log.Fatalf("Could not start server: %s\n", err)
	}
}
