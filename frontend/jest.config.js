module.exports = {
  preset: 'ts-jest',
  testEnvironment: 'jsdom', // Use jsdom for DOM-related testing
  setupFilesAfterEnv: ['@testing-library/jest-dom/extend-expect'], // For additional DOM matchers
  moduleNameMapper: {
    // If you have module aliases in tsconfig.json, map them here
    // Example: '^@/(.*)$': '<rootDir>/src/$1'
  },
  globals: {
    'ts-jest': {
      tsconfig: 'tsconfig.json', // Ensure it uses your tsconfig
    },
  },
};
