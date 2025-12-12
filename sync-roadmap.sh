#!/bin/bash

echo "# Penny API - Roadmap de Desenvolvimento" > ROADMAP.md
echo "" >> ROADMAP.md
echo "Última atualização: $(date '+%Y-%m-%d %H:%M')" >> ROADMAP.md
echo "" >> ROADMAP.md

# Exportar issues abertas
echo "## 📋 Issues Abertas" >> ROADMAP.md
gh issue list --state open --json number,title,body,labels --jq '.[] | "### #\(.number) - \(.title)\n\n\(.body)\n\n---\n"' >> ROADMAP.md

# Exportar issues fechadas
echo "## ✅ Issues Concluídas" >> ROADMAP.md
gh issue list --state closed --json number,title --jq '.[] | "- [x] #\(.number) - \(.title)"' >> ROADMAP.md

# Para tornar executável:
# chmod +x sync-roadmap.sh
# ./sync-roadmap.sh