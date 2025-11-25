/**
 * @fileoverview Unit tests for graph-related utilities including:
 * - Graph class (addCity, addEdge, neighbors)
 * - validateGraphData
 * - buildGraph
 * - getNearbyCities
 *
 * These tests ensure that the graph module behaves consistently, handles errors,
 * and correctly builds graph structures used for visualizations or calculations.
 */

import {
    Graph,
    validateGraphData,
    buildGraph,
    getNearbyCities,
    sampleData
} from "./graph.js";

// ──────────────────────────────────────────────
// GRAPH CLASS TESTS
// ──────────────────────────────────────────────

/**
 * Tests for the {@link Graph} class implementation.
 */
describe("Graph class", () => {

    /**
     * Ensures that addCity() inserts a new city into the adjacency list.
     */
    test("addCity should add new cities", () => {
        const g = new Graph();
        g.addCity("GDL");
        expect(g.adj.has("GDL")).toBe(true);
    });

    /**
     * Ensures addCity() rejects invalid names, such as empty or null inputs.
     */
    test("addCity should reject invalid names", () => {
        const g = new Graph();
        expect(() => g.addCity("")).toThrow("Invalid city name");
        expect(() => g.addCity(null)).toThrow("Invalid city name");
    });

    /**
     * Ensures addEdge() connects two valid cities and records distance correctly.
     */
    test("addEdge should add edges between existing cities", () => {
        const g = new Graph();
        g.addCity("A");
        g.addCity("B");
        g.addEdge("A", "B", 10);

        expect(g.neighbors("A")[0]).toEqual({ to: "B", distance: 10 });
        expect(g.neighbors("B")[0]).toEqual({ to: "A", distance: 10 });
    });

    /**
     * Ensures addEdge() fails when referencing unknown cities.
     */
    test("addEdge should reject unknown cities", () => {
        const g = new Graph();
        g.addCity("A");

        expect(() => g.addEdge("A", "C", 10)).toThrow("Unknown city");
    });

    /**
     * Ensures addEdge() rejects invalid distances such as negative numbers or NaN.
     */
    test("addEdge should reject invalid distances", () => {
        const g = new Graph();
        g.addCity("A");
        g.addCity("B");

        expect(() => g.addEdge("A", "B", -5)).toThrow("Invalid distance");
        expect(() => g.addEdge("A", "B", NaN)).toThrow("Invalid distance");
    });

    /**
     * Ensures neighbors() throws an error when called with a non-existent city.
     */
    test("neighbors should fail for unknown city", () => {
        const g = new Graph();
        g.addCity("A");

        expect(() => g.neighbors("B")).toThrow("Unknown city");
    });
});

// ──────────────────────────────────────────────
// validateGraphData TESTS
// ──────────────────────────────────────────────

/**
 * Tests for validateGraphData(), which validates raw graph dataset structure.
 */
describe("validateGraphData", () => {

    /**
     * Ensures the validator accepts a correct dataset.
     */
    test("should validate correct dataset", () => {
        const res = validateGraphData(sampleData);
        expect(res.ok).toBe(true);
    });

    /**
     * Ensures duplicate city names trigger a validation failure.
     */
    test("should fail for duplicate cities", () => {
        const data = { cities: ["A", "A"], edges: [] };
        expect(validateGraphData(data)).toEqual({ ok: false, reason: "duplicate cities" });
    });

    /**
     * Ensures invalid city strings produce a validation error.
     */
    test("should fail for invalid city entry", () => {
        const data = { cities: ["Valid", ""], edges: [] };
        expect(validateGraphData(data)).toEqual({ ok: false, reason: "invalid city entry" });
    });

    /**
     * Ensures edges that reference unknown cities are rejected.
     */
    test("should fail for unknown city in edges", () => {
        const data = {
            cities: ["A"],
            edges: [{ from: "A", to: "B", distance: 10 }]
        };
        expect(validateGraphData(data))
            .toEqual({ ok: false, reason: "edge references unknown city" });
    });

    /**
     * Ensures distances below zero fail validation.
     */
    test("should fail for invalid distance", () => {
        const data = {
            cities: ["A", "B"],
            edges: [{ from: "A", to: "B", distance: -1 }]
        };
        expect(validateGraphData(data)).toEqual({ ok: false, reason: "invalid distance" });
    });
});

// ──────────────────────────────────────────────
// buildGraph TESTS
// ──────────────────────────────────────────────

/**
 * Tests for buildGraph(), which constructs a Graph instance from data arrays.
 */
describe("buildGraph", () => {

    /**
     * Ensures buildGraph creates all cities and edges correctly.
     */
    test("should build graph with all cities and edges", () => {
        const g = buildGraph(sampleData.cities, sampleData.edges);
        expect(g.adj.size).toBe(sampleData.cities.length);

        const neighbors = g.neighbors("Guadalajara");
        expect(neighbors.length).toBeGreaterThan(0);
    });
});

// ──────────────────────────────────────────────
// getNearbyCities TESTS
// ──────────────────────────────────────────────

/**
 * Tests for getNearbyCities(), which filters and sorts neighboring cities
 * by distance from a given starting point.
 */
describe("getNearbyCities", () => {

    /**
     * Ensures nearby cities are sorted by distance ascending.
     */
    test("should return nearby cities sorted by distance", () => {
        const g = buildGraph(sampleData.cities, sampleData.edges);
        const result = getNearbyCities(g, "Guadalajara", 100);

        expect(result[0].city).toBe("Tlaquepaque");
        expect(result[0].distance).toBe(10);
    });

    /**
     * Ensures unknown cities result in an empty list instead of an error.
     */
    test("should return empty array for unknown city", () => {
        const g = buildGraph(sampleData.cities, sampleData.edges);
        expect(getNearbyCities(g, "XYZ")).toEqual([]);
    });

    /**
     * Ensures filtering by maxDistance removes far cities.
     */
    test("should respect maxDistance", () => {
        const g = buildGraph(sampleData.cities, sampleData.edges);
        const result = getNearbyCities(g, "Guadalajara", 20);

        const cities = result.map(r => r.city);
        expect(cities).toContain("Tlaquepaque");
        expect(cities).toContain("Zapopan");
        expect(cities).not.toContain("Tepatitlán");
    });

    /**
     * Ensures getNearbyCities() fails loudly when passed a non-Graph object.
     */
    test("should throw if graph is not Graph", () => {
        expect(() => getNearbyCities({}, "A")).toThrow("graph must be Graph");
    });
});
