export default {
  testEnvironment: "node",
  collectCoverage: true,
  collectCoverageFrom: ["js/**/graph.js"],
  coverageReporters: ["text", "lcov"]
};