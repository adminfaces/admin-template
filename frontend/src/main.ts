// Item type (optional, but good for type safety)
interface Item {
  id: number;
  name: string;
}

const API_BASE_URL = 'http://localhost:8080/api';

// UI Elements
const itemsList = document.getElementById('items-list') as HTMLUListElement;
const itemNameInput = document.getElementById('item-name-input') as HTMLInputElement;
const addItemButton = document.getElementById('add-item-button') as HTMLButtonElement;

// Function to fetch items from the backend
async function fetchItems(): Promise<Item[]> {
  try {
    const response = await fetch(`${API_BASE_URL}/items`);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status} ${response.statusText}`);
    }
    const items: Item[] = await response.json();
    return items;
  } catch (error) {
    console.error('Error fetching items:', error);
    return []; // Return empty array on error
  }
}

// Function to render items in the list
function renderItems(items: Item[]): void {
  if (!itemsList) return;

  itemsList.innerHTML = ''; // Clear existing items

  if (items.length === 0) {
    const li = document.createElement('li');
    li.textContent = 'No items found.';
    itemsList.appendChild(li);
    return;
  }

  items.forEach(item => {
    const li = document.createElement('li');
    li.textContent = `ID: ${item.id}, Name: ${item.name}`;
    itemsList.appendChild(li);
  });
}

// Function to add a new item
async function addItem(name: string): Promise<Item | null> {
  if (!name.trim()) {
    alert('Item name cannot be empty.');
    return null;
  }
  try {
    const response = await fetch(`${API_BASE_URL}/items`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ name }),
    });
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({ message: response.statusText }));
      throw new Error(`HTTP error! status: ${response.status} - ${errorData.message || response.statusText}`);
    }
    const newItem: Item = await response.json();
    return newItem;
  } catch (error) {
    console.error('Error adding item:', error);
    alert(`Failed to add item: ${error}`);
    return null;
  }
}

// Event listener for the "Add Item" button
async function handleAddItem(): Promise<void> {
  if (!itemNameInput || !addItemButton) return;

  const itemName = itemNameInput.value;
  addItemButton.disabled = true; // Disable button to prevent multiple clicks

  const newItem = await addItem(itemName);

  if (newItem) {
    // Refresh the list to show the new item
    const updatedItems = await fetchItems();
    renderItems(updatedItems);
    itemNameInput.value = ''; // Clear input field
  }
  addItemButton.disabled = false; // Re-enable button
}

// Initial actions when the page loads
async function initializeApp(): Promise<void> {
  if (!itemsList || !itemNameInput || !addItemButton) {
    console.error('Required UI elements not found!');
    return;
  }

  // Add event listener
  addItemButton.addEventListener('click', handleAddItem);

  // Fetch and render initial items
  const initialItems = await fetchItems();
  renderItems(initialItems);

  // Also log backend health for good measure (optional, from previous step)
  checkBackendHealth();
}

// Backend health check function (can be kept or removed if not needed for this specific task)
async function checkBackendHealth() {
  try {
    const response = await fetch('http://localhost:8080/api/health');
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    console.log('Backend health:', data);
  } catch (error) {
    console.error('Error fetching backend health:', error);
  }
}

// Start the application
initializeApp();
