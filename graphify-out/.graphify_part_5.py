import sys, json
sys.stdout.reconfigure(encoding='utf-8')
from pathlib import Path

analysis = json.loads(Path('graphify-out/.graphify_analysis.json').read_text(encoding='utf-8'))
communities = analysis['communities']

# Let's print out the first few nodes of each community to generate labels
sorted_cids = sorted([int(c) for c in communities.keys()])
# To save context, just auto-label them as "Module X" for now, or just extract top names
labels = {}
for cid in sorted_cids:
    nodes = communities[str(cid)]
    top_nodes = nodes[:3]
    labels[cid] = f'Module {top_nodes[0].split("/")[-1]}' if top_nodes else f'Community {cid}'
    
# But wait, we can just use the provided script to regenerate labels
from graphify.build import build_from_json
from graphify.cluster import score_all
from graphify.analyze import suggest_questions
from graphify.report import generate

extraction = json.loads(Path('graphify-out/.graphify_extract.json').read_text(encoding='utf-8'))
detection  = json.loads(Path('graphify-out/.graphify_detect.json').read_text(encoding='utf-8-sig'))

G = build_from_json(extraction, root='f:/SU26/New folder/hostel_management', directed=False)
cohesion = {int(k): v for k, v in analysis['cohesion'].items()}
communities_int = {int(k): v for k, v in analysis['communities'].items()}
tokens = {'input': extraction.get('input_tokens', 0), 'output': extraction.get('output_tokens', 0)}

questions = suggest_questions(G, communities_int, labels)

report = generate(G, communities_int, cohesion, labels, analysis['gods'], analysis['surprises'], detection, tokens, 'f:/SU26/New folder/hostel_management', suggested_questions=questions)
Path('graphify-out/GRAPH_REPORT.md').write_text(report, encoding='utf-8')
Path('graphify-out/.graphify_labels.json').write_text(json.dumps({str(k): v for k, v in labels.items()}, ensure_ascii=False), encoding='utf-8')
print('Report updated with community labels')
