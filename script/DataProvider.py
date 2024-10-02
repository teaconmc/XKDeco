import json
from pathlib import Path

import yaml

from ResourceLocation import ResourceLocation


class DataProvider:
    def __init__(self, pack, identifier: str, dataPath: str):
        self.pack = pack
        self.identifier = identifier
        self.dataPath = dataPath
        self.dataPath = pack.packVersion.resolvePath(pack, self)
        self.prettyPrint = pack.config['json_pretty_print']
        self.exportFormat = pack.config['export_format']
        self.count = 0

    def writeFile(self, file: ResourceLocation, data: any):
        file = self.pack.toAbsPath(self, file)
        Path(file).parent.mkdir(parents=True, exist_ok=True)
        with open(file, 'w', encoding='utf-8') as f:
            if self.exportFormat == 'json':
                json.dump(data, f, indent=2 if self.prettyPrint else None, ensure_ascii=False)
            elif self.exportFormat == 'yaml':
                yaml.dump(data, f, indent=2 if self.prettyPrint else None, sort_keys=False, allow_unicode=True)
            else:
                raise ValueError('Unsupported export format: ' + self.exportFormat)
            self.count += 1

    def generate(self):
        pass

    def canGenerate(self) -> bool:
        return True

    def __str__(self):
        return self.__class__.__name__
