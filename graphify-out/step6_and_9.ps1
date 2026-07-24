$python = (Get-Content graphify-out\.graphify_python)

$env:Path = "C:\Users\Admin\.local\bin;$env:Path"
graphify export html

& $python -c "
import json
from pathlib import Path
from datetime import datetime, timezone
from graphify.detect import save_manifest

detect = json.loads(Path('graphify-out/.graphify_detect.json').read_text(encoding='utf-8-sig'))
save_manifest(detect.get('all_files') or detect['files'], root='.')

extract = json.loads(Path('graphify-out/.graphify_extract.json').read_text(encoding='utf-8-sig'))
input_tok = extract.get('input_tokens', 0)
output_tok = extract.get('output_tokens', 0)

cost_path = Path('graphify-out/cost.json')
if cost_path.exists():
    cost = json.loads(cost_path.read_text(encoding='utf-8-sig'))
else:
    cost = {'runs': [], 'total_input_tokens': 0, 'total_output_tokens': 0}

cost['runs'].append({
    'date': datetime.now(timezone.utc).isoformat(),
    'input_tokens': input_tok,
    'output_tokens': output_tok,
    'files': detect.get('total_files', 0),
})
cost['total_input_tokens'] += input_tok
cost['total_output_tokens'] += output_tok
cost_path.write_text(json.dumps(cost, indent=2, ensure_ascii=False), encoding='utf-8')

print(f'This run: {input_tok:,} input tokens, {output_tok:,} output tokens')
print(f'All time: {cost[\"total_input_tokens\"]:,} input, {cost[\"total_output_tokens\"]:,} output ({len(cost[\"runs\"])} runs)')
"

Remove-Item -Path graphify-out\.graphify_detect.json, graphify-out\.graphify_extract.json, graphify-out\.graphify_ast.json, graphify-out\.graphify_semantic.json, graphify-out\.graphify_analysis.json -ErrorAction SilentlyContinue
Get-ChildItem -Path graphify-out -Filter ".graphify_chunk_*.json" -Recurse -ErrorAction SilentlyContinue | Remove-Item -ErrorAction SilentlyContinue
Remove-Item -Path graphify-out\.needs_update -ErrorAction SilentlyContinue
