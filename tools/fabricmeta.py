"""Fabric mod metadata helpers: fabric.mod.json extraction (incl. nested jars) and version-predicate evaluation.

Implements the subset of Fabric Loader's SemanticVersion / VersionPredicate rules used by real mods:
operators =, >, >=, <, <=, ~, ^, '*', space-separated AND, list OR, 'x' wildcards, and the '-' suffix
meaning "lowest pre-release". Non-semantic versions fall back to string equality (as Fabric does).
"""
import io
import json
import re
import zipfile

_SEMVER = re.compile(r"^(\d+(?:\.\d+)*)(?:-([0-9A-Za-z.-]*))?(?:\+.*)?$")


class Version:
    def __init__(self, s):
        self.raw = s
        m = _SEMVER.match(s.strip())
        self.semantic = bool(m)
        if m:
            self.core = [int(x) for x in m.group(1).split(".")]
            self.pre = m.group(2)  # None = release; "" = lowest pre-release
        else:
            self.core, self.pre = None, None

    def cmp(self, other):
        if not (self.semantic and other.semantic):
            return (self.raw > other.raw) - (self.raw < other.raw)
        n = max(len(self.core), len(other.core))
        a = self.core + [0] * (n - len(self.core))
        b = other.core + [0] * (n - len(other.core))
        if a != b:
            return (a > b) - (a < b)
        if self.pre is None and other.pre is None:
            return 0
        if self.pre is None:
            return 1
        if other.pre is None:
            return -1
        pa, pb = self.pre.split(".") if self.pre else [], other.pre.split(".") if other.pre else []
        for x, y in zip(pa, pb):
            if x == y:
                continue
            if x.isdigit() and y.isdigit():
                return (int(x) > int(y)) - (int(x) < int(y))
            if x.isdigit():
                return -1
            if y.isdigit():
                return 1
            return (x > y) - (x < y)
        return (len(pa) > len(pb)) - (len(pa) < len(pb))


def _term_matches(term, v):
    term = term.strip()
    if term in ("*", ""):
        return True
    for op in (">=", "<=", ">", "<", "=", "~", "^"):
        if term.startswith(op):
            target = term[len(op):].strip()
            break
    else:
        op, target = "=", term
    core = target.split("+")[0].split("-")[0]
    if any(p.lower() in ("x", "*") for p in core.split(".")):
        parts = core.split(".")
        fixed = []
        for p in parts:
            if p.lower() in ("x", "*"):
                break
            fixed.append(p)
        if not v.semantic:
            return False
        return v.core[: len(fixed)] == [int(p) for p in fixed]
    t = Version(target)
    c = v.cmp(t)
    if op == "=":
        return c == 0 if t.semantic else v.raw == target
    if op == ">=":
        return c >= 0
    if op == ">":
        return c > 0
    if op == "<=":
        return c <= 0
    if op == "<":
        return c < 0
    if not (t.semantic and v.semantic):
        return False
    if op == "~":  # same major.minor, >= target
        upper = list(t.core[:2]) + ([0] if len(t.core) < 2 else [])
        upper = [upper[0], upper[1] + 1] if len(t.core) >= 2 else [upper[0] + 1]
        return c >= 0 and Version(".".join(map(str, upper)) + "-").cmp(v) > 0
    if op == "^":  # same major, >= target
        return c >= 0 and v.core[0] == t.core[0]
    return False


def predicate_matches(pred, version_str):
    """pred: str or list[str] (list = OR). Space-separated terms inside a string = AND."""
    v = Version(version_str)
    preds = pred if isinstance(pred, list) else [pred]
    return any(all(_term_matches(t, v) for t in p.split()) for p in preds)


def read_mod_jsons(jar_bytes, path="<root>", depth=0):
    """Return list of (fabric.mod.json dict, path) for the jar and all nested jars."""
    out = []
    try:
        z = zipfile.ZipFile(io.BytesIO(jar_bytes))
    except zipfile.BadZipFile:
        return out
    if "fabric.mod.json" in z.namelist():
        meta = json.loads(z.read("fabric.mod.json").decode("utf-8-sig"), strict=False)
        out.append((meta, path))
        for j in meta.get("jars", []) or []:
            f = j.get("file")
            if f and f in z.namelist() and depth < 4:
                out.extend(read_mod_jsons(z.read(f), f"{path}!/{f}", depth + 1))
    return out
