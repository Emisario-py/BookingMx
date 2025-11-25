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

describe("Graph class", () => {
    test("addCity should add new cities", () => {
        const g = new Graph();
        g.addCity("GDL");
        expect(g.adj.has("GDL")).toBe(true);
    });

    test("addCity should reject invalid names", () => {
        const g = new Graph();
        expect(() => g.addCity("")).toThrow("Invalid city name");
        expect(() => g.addCity(null)).toThrow("Invalid city name");
    });

    test("addEdge should add edges between existing cities", () => {
        const g = new Graph();
        g.addCity("A");
        g.addCity("B");
        g.addEdge("A", "B", 10);

        expect(g.neighbors("A")[0]).toEqual({ to: "B", distance: 10 });
        expect(g.neighbors("B")[0]).toEqual({ to: "A", distance: 10 });
    });

    test("addEdge should reject unknown cities", () => {
        const g = new Graph();
        g.addCity("A");

        expect(() => g.addEdge("A", "C", 10)).toThrow("Unknown city");
    });

    test("addEdge should reject invalid distances", () => {
        const g = new Graph();
        g.addCity("A");
        g.addCity("B");

        expect(() => g.addEdge("A", "B", -5)).toThrow("Invalid distance");
        expect(() => g.addEdge("A", "B", NaN)).toThrow("Invalid distance");
    });

    test("neighbors should fail for unknown city", () => {
        const g = new Graph();
        g.addCity("A");

        expect(() => g.neighbors("B")).toThrow("Unknown city");
    });
});

// ──────────────────────────────────────────────
// validateGraphData TESTS
// ──────────────────────────────────────────────

describe("validateGraphData", () => {
    test("should validate correct dataset", () => {
        const res = validateGraphData(sampleData);
        expect(res.ok).toBe(true);
    });

    test("should fail for duplicate cities", () => {
        const data = {
            cities: ["A", "A"],
            edges: []
        };
        expect(validateGraphData(data)).toEqual({ ok: false, reason: "duplicate cities" });
    });

    test("should fail for invalid city entry", () => {
        const data = {
            cities: ["Valid", ""],
            edges: []
        };
        expect(validateGraphData(data)).toEqual({ ok: false, reason: "invalid city entry" });
    });

    test("should fail for unknown city in edges", () => {
        const data = {
            cities: ["A"],
            edges: [{ from: "A", to: "B", distance: 10 }]
        };
        expect(validateGraphData(data)).toEqual({ ok: false, reason: "edge references unknown city" });
    });

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

describe("buildGraph", () => {
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

describe("getNearbyCities", () => {
    test("should return nearby cities sorted by distance", () => {
        const g = buildGraph(sampleData.cities, sampleData.edges);
        const result = getNearbyCities(g, "Guadalajara", 100);

        expect(result[0].city).toBe("Tlaquepaque");
        expect(result[0].distance).toBe(10);
    });

    test("should return empty array for unknown city", () => {
        const g = buildGraph(sampleData.cities, sampleData.edges);
        expect(getNearbyCities(g, "XYZ")).toEqual([]);
    });

    test("should respect maxDistance", () => {
        const g = buildGraph(sampleData.cities, sampleData.edges);
        const result = getNearbyCities(g, "Guadalajara", 20);

        const cities = result.map(r => r.city);
        expect(cities).toContain("Tlaquepaque");
        expect(cities).toContain("Zapopan");
        expect(cities).not.toContain("Tepatitlán");
    });

    test("should throw if graph is not Graph", () => {
        expect(() => getNearbyCities({}, "A")).toThrow("graph must be Graph");
    });
});
