#!/usr/bin/env python3
"""Probe Modrinth for Fabric support of candidate projects across target MC versions.

Usage: python3 -I tools/modrinth_probe.py candidates.txt out.json
candidates.txt: one slug per line, '#' comments allowed, 'category: slug' allowed.
Only Modrinth's official API (api.modrinth.com/v2) is queried.
"""
import re, json, sys, time, urllib.request, urllib.parse, datetime

API = "https://api.modrinth.com/v2"
UA = {"User-Agent": "jules-homestead-modpack-research/0.1 (github.com/julliancm07-png/jules-homestead)"}
TARGETS = ["26.3", "26.2", "26.1.2", "26.1.1", "26.1", "1.21.11", "1.21.10", "1.21.8", "1.21.5", "1.21.4", "1.21.1", "1.20.1"]

def get(path, params=None):
    url = API + path + ("?" + urllib.parse.urlencode(params) if params else "")
    for attempt in range(5):
        try:
            with urllib.request.urlopen(urllib.request.Request(url, headers=UA), timeout=60) as r:
                return json.load(r)
        except urllib.error.HTTPError as e:
            if e.code == 404:
                return None
            if e.code == 429:
                time.sleep(10); continue
            raise
        except Exception:
            time.sleep(2 ** attempt)
    raise RuntimeError(url)

def probe(slug):
    p = get(f"/project/{slug}")
    if p is None:
        return {"slug": slug, "missing": True}
    vers = get(f"/project/{p['id']}/version", {"loaders": json.dumps(["fabric"])}) or []
    support = {}
    for t in TARGETS:
        hits = [v for v in vers if t in v["game_versions"]]
        if hits:
            v = max(hits, key=lambda v: v["date_published"])
            support[t] = {"version": v["version_number"], "id": v["id"], "date": v["date_published"][:10], "type": v["version_type"]}
    rel = {g for v in vers for g in v["game_versions"] if re.fullmatch(r"\d+(\.\d+)+", g)}
    newest_fabric = sorted(rel, key=lambda s: tuple(int(x) for x in s.split(".")))[-3:]
    return {"slug": p["slug"], "id": p["id"], "title": p["title"], "loaders": p["loaders"],
            "client_side": p["client_side"], "server_side": p["server_side"],
            "license": p["license"]["id"], "updated": p["updated"][:10], "downloads": p["downloads"],
            "project_type": p["project_type"], "fabric_support": support, "newest_fabric_gv": newest_fabric}

def main():
    cands = []
    for line in open(sys.argv[1]):
        line = line.split("#")[0].strip()
        if not line: continue
        cat, _, slug = line.rpartition(":")
        cands.append((cat.strip() or "misc", slug.strip()))
    out = {"queried_at": datetime.datetime.now(datetime.timezone.utc).isoformat(), "source": API, "targets": TARGETS, "projects": []}
    for cat, slug in cands:
        r = probe(slug); r["category"] = cat
        out["projects"].append(r)
        fs = r.get("fabric_support", {})
        print(f"{cat:12} {slug:40} " + ("MISSING" if r.get("missing") else " ".join(("Y" if t in fs else ".") for t in TARGETS) + f"  newest={r['newest_fabric_gv'][-1:] }"), flush=True)
    json.dump(out, open(sys.argv[2], "w"), indent=1)

if __name__ == "__main__":
    main()
