import os
import re

VIEWS_DIR = 'src/main/webapp/WEB-INF/views'

replacements = [
    # Add hero gradient to page-header
    (r'class="page-header"', r'class="page-header hero-sky-gradient"'),
    # Upgrade Buttons to Mintlify style
    (r'\bbtn-hms-primary\b', 'btn-mintlify-primary'),
    (r'\bbtn-hms-outline\b', 'btn-mintlify-secondary'),
    # Upgrade Data Panels & Widgets to Surfaces
    (r'\bdata-panel\b', 'data-surface'),
    (r'\bwidget\b', 'widget-surface'),
    (r'\bwidget-header\b', 'widget-surface-header'),
    (r'\bwidget-body\b', 'widget-surface-body'),
    (r'\bdata-panel-header\b', 'data-surface-header'),
    # Upgrade KPI Cards
    (r'\bkpi-card\b', 'kpi-surface-card'),
    # Tables
    (r'\btable-hms\b', 'table-mintlify'),
    # Remove old bootstrap utilities that ruin flat design
    (r'\bshadow\b', ''),
    (r'\brounded\b', ''),
    (r'\bbg-light\b', ''),
    (r'\bbg-white\b', ''),
    # Clean up empty spaces in class attributes
    (r'class="\s+', 'class="'),
    (r'\s+"', '"'),
    (r'class=""', '')
]

count = 0

for root, dirs, files in os.walk(VIEWS_DIR):
    for file in files:
        if file.endswith('.jsp'):
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
            
            original_content = content
            for old, new in replacements:
                content = re.sub(old, new, content)
            
            if content != original_content:
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(content)
                count += 1

print(f"Refactored {count} JSP files successfully!")
