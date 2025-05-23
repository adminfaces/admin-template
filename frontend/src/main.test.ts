import { JSDOM } from 'jsdom';
import { fetchItems, renderItems, addItem, initializeApp } from './main'; // Assuming these are exported or we test their effects

// Mock global fetch
global.fetch = jest.fn();

// Setup a basic DOM environment for each test
let dom: JSDOM;
let document: Document;

beforeEach(() => {
  dom = new JSDOM(`
    <!DOCTYPE html>
    <html>
    <body>
      <div id="app">
        <h1>Item Management</h1>
        <div>
          <input type="text" id="item-name-input" placeholder="Enter item name">
          <button id="add-item-button">Add Item</button>
        </div>
        <h2>Items</h2>
        <ul id="items-list"></ul>
      </div>
      <script type="module" src="/src/main.ts"></script> 
    </body>
    </html>`,
    { url: "http://localhost" } // Needed for JSDOM to handle module scripts if they were real
  );
  document = dom.window.document;
  global.document = document; // Set the global document for main.ts to use
  global.window = dom.window as unknown as Window & typeof globalThis; // Set global window
  global.alert = jest.fn(); // Mock alert

  // Reset mocks before each test
  (global.fetch as jest.Mock).mockClear();
  (global.alert as jest.Mock).mockClear();
});


describe('Item Management Frontend', () => {
  describe('initializeApp', () => {
    it('should fetch items and attempt to render them on initialization', async () => {
      // Mock fetch to return an empty array for items
      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => [],
      } as Response);
      // Mock fetch for health check (if initializeApp calls it)
      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ status: 'ok' }),
      } as Response);


      // Spy on renderItems to see if it's called
      // We need a way to spy on renderItems or check its effects.
      // Since renderItems manipulates the DOM, we can check the DOM.
      // Or, if main.ts was structured to make renderItems easily spyable, we could use jest.spyOn.
      // For now, let's check if fetch was called for items.

      await initializeApp(); // Call the main initialization function

      expect(global.fetch).toHaveBeenCalledWith('http://localhost:8080/api/items');
      
      // Check if the "No items found." message is rendered
      const itemsList = document.getElementById('items-list');
      expect(itemsList).not.toBeNull();
      if (itemsList) {
        expect(itemsList.textContent).toContain('No items found.');
      }
    });
  });

  describe('fetchItems', () => {
    it('should return items from the API', async () => {
      const mockItems = [{ id: 1, name: 'Test Item 1' }];
      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => mockItems,
      } as Response);

      const items = await fetchItems();
      expect(global.fetch).toHaveBeenCalledWith('http://localhost:8080/api/items');
      expect(items).toEqual(mockItems);
    });

    it('should return an empty array if the fetch fails', async () => {
      (global.fetch as jest.Mock).mockRejectedValueOnce(new Error('Network error'));
      const items = await fetchItems();
      expect(items).toEqual([]);
    });
  });

  describe('addItem', () => {
    it('should POST a new item and return it', async () => {
      const newItem = { name: 'New Test Item' };
      const mockResponseItem = { id: 2, ...newItem };
      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        status: 201,
        json: async () => mockResponseItem,
      } as Response);

      const addedItem = await addItem(newItem.name);
      expect(global.fetch).toHaveBeenCalledWith('http://localhost:8080/api/items', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newItem),
      });
      expect(addedItem).toEqual(mockResponseItem);
    });

    it('should return null and alert if item name is empty', async () => {
        const addedItem = await addItem('');
        expect(addedItem).toBeNull();
        expect(global.alert).toHaveBeenCalledWith('Item name cannot be empty.');
        expect(global.fetch).not.toHaveBeenCalled();
    });


    it('should return null if the POST request fails', async () => {
      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: false,
        status: 500,
        statusText: "Internal Server Error",
        json: async () => ({ message: "Internal Server Error" }),
      } as Response);

      const addedItem = await addItem('Bad Item');
      expect(addedItem).toBeNull();
      expect(global.alert).toHaveBeenCalledWith(expect.stringContaining('Failed to add item: Error: HTTP error! status: 500 - Internal Server Error'));
    });
  });
  
  describe('renderItems', () => {
    it('should display "No items found." when items array is empty', () => {
      const itemsList = document.getElementById('items-list') as HTMLUListElement;
      renderItems([]);
      expect(itemsList.innerHTML).toContain('<li>No items found.</li>');
    });

    it('should render list of items', () => {
      const itemsList = document.getElementById('items-list') as HTMLUListElement;
      const mockItems = [
        { id: 1, name: 'Item A' },
        { id: 2, name: 'Item B' },
      ];
      renderItems(mockItems);
      expect(itemsList.children.length).toBe(2);
      expect(itemsList.children[0].textContent).toBe('ID: 1, Name: Item A');
      expect(itemsList.children[1].textContent).toBe('ID: 2, Name: Item B');
    });
  });

});

// Note: To run these tests, you might need to adjust `main.ts` to export functions
// or structure it in a way that `initializeApp` and other functions can be called and spied upon.
// If `main.ts` primarily adds event listeners and calls functions internally,
// you might need to trigger events (e.g., button clicks) and observe DOM changes.
// The current tests assume `initializeApp`, `fetchItems`, `renderItems`, `addItem` are exportable and testable.
// The `initializeApp` test also checks a side effect on the DOM.
// The JSDOM setup provides a basic DOM. `global.document` and `global.window` are set.
// `global.alert` is mocked.
// The script `/src/main.ts` in the JSDOM setup is illustrative; tests import functions directly.
