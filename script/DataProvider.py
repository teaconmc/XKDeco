import json
from pathlib import Path

from ResourceLocation import ResourceLocation


class DataProvider:
    def __init__(self, pack, identifier: str, dataPath: str):
        self.pack = pack
        self.identifier = identifier
        self.dataPath = dataPath
        self.prettyPrint = pack.config['json_pretty_print']
        self.count = 0

    def writeJson(self, file: ResourceLocation, data: dict):
        file = self.pack.toAbsPath(self.dataPath, file, '.json')
        Path(file).parent.mkdir(parents=True, exist_ok=True)
        with open(file, 'w', encoding='utf-8') as f:
            json.dump(data, f, indent=2 if self.prettyPrint else None, ensure_ascii=False)
            self.count += 1

    def generate(self):
        pass

    def __str__(self):
        return self.__class__.__name__
