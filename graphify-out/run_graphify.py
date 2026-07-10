import sys, json
from graphify.extract import collect_files, extract
from pathlib import Path

code_files = []
detect = json.loads(Path("graphify-out/.graphify_detect.json").read_text(encoding="utf-8-sig"))
for f in detect.get("files", {}).get("code", []):
    code_files.extend(collect_files(Path(f)) if Path(f).is_dir() else [Path(f)])

if code_files:
    result = extract(code_files, cache_root=Path("."))
    Path("graphify-out/.graphify_ast.json").write_text(json.dumps(result, indent=2, ensure_ascii=False), encoding="utf-8-sig")
    print(f"AST: {len(result['nodes'])} nodes, {len(result['edges'])} edges")
else:
    Path("graphify-out/.graphify_ast.json").write_text(json.dumps({"nodes":[],"edges":[],"input_tokens":0,"output_tokens":0}, ensure_ascii=False), encoding="utf-8-sig")
    print("No code files - skipping AST extraction")

Path("graphify-out/.graphify_semantic.json").write_text(json.dumps({"nodes":[],"edges":[],"hyperedges":[],"input_tokens":0,"output_tokens":0}), encoding="utf-8-sig")

ast = json.loads(Path("graphify-out/.graphify_ast.json").read_text(encoding="utf-8-sig"))
sem = json.loads(Path("graphify-out/.graphify_semantic.json").read_text(encoding="utf-8-sig"))

seen = {n["id"] for n in ast["nodes"]}
merged_nodes = list(ast["nodes"])
for n in sem["nodes"]:
    if n["id"] not in seen:
        merged_nodes.append(n)
        seen.add(n["id"])

merged_edges = ast["edges"] + sem["edges"]
merged_hyperedges = sem.get("hyperedges", [])
merged = {
    "nodes": merged_nodes,
    "edges": merged_edges,
    "hyperedges": merged_hyperedges,
    "input_tokens": sem.get("input_tokens", 0),
    "output_tokens": sem.get("output_tokens", 0),
}
Path("graphify-out/.graphify_extract.json").write_text(json.dumps(merged, indent=2, ensure_ascii=False), encoding="utf-8-sig")
print(f"Merged: {len(merged_nodes)} nodes, {len(merged_edges)} edges")

from graphify.build import build_from_json
from graphify.cluster import cluster, score_all
from graphify.analyze import god_nodes, surprising_connections, suggest_questions
from graphify.report import generate
from graphify.export import to_json

extraction = json.loads(Path("graphify-out/.graphify_extract.json").read_text(encoding="utf-8-sig"))
detection  = json.loads(Path("graphify-out/.graphify_detect.json").read_text(encoding="utf-8-sig"))

G = build_from_json(extraction, root=".", directed=False)
if G.number_of_nodes() == 0:
    print("ERROR: Graph is empty - extraction produced no nodes.")
    sys.exit(1)
communities = cluster(G)
cohesion = score_all(G, communities)
tokens = {"input": extraction.get("input_tokens", 0), "output": extraction.get("output_tokens", 0)}
gods = god_nodes(G)
surprises = surprising_connections(G, communities)
labels = {cid: "Community " + str(cid) for cid in communities}
questions = suggest_questions(G, communities, labels)

wrote = to_json(G, communities, "graphify-out/graph.json")
if not wrote:
    print("ERROR: refused to shrink graphify-out/graph.json")
    sys.exit(1)
report = generate(G, communities, cohesion, labels, gods, surprises, detection, tokens, ".", suggested_questions=questions)
Path("graphify-out/GRAPH_REPORT.md").write_text(report, encoding="utf-8-sig")
analysis = {
    "communities": {str(k): v for k, v in communities.items()},
    "cohesion": {str(k): v for k, v in cohesion.items()},
    "gods": gods,
    "surprises": surprises,
    "questions": questions,
}
Path("graphify-out/.graphify_analysis.json").write_text(json.dumps(analysis, indent=2, ensure_ascii=False), encoding="utf-8-sig")
print(f"Graph: {G.number_of_nodes()} nodes, {G.number_of_edges()} edges, {len(communities)} communities")

from graphify.detect import save_manifest
save_manifest(detect.get("all_files") or detect["files"], root=".")
