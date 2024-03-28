def removeBOM(file):
    # Remove BOM from file
    with open(file, 'r', encoding='utf-8') as f:
        data = f.read()
    if data.startswith('\ufeff'):
        data = data[1:]
        with open(file, 'w', encoding='utf-8') as f:
            f.write(data)

def defaulted(id: str, default: str) -> str:
    if ':' in id:
        return id
    else:
        return default + ':' + id

def trimRL(id: str, default: str) -> str:
    if id.startswith(default + ':'):
        return id[len(default) + 1:]
    else:
        return id