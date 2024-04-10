import json
from pathlib import Path

import yaml

from DataProvider import DataProvider
from ResourceLocation import ResourceLocation


class ShapeProvider(DataProvider):
    def __init__(self, pack):
        super().__init__(pack, 'shape', 'assets/{}/kiwi/shape')
        self.shapes = {}

    def generate(self):
        if 'shapes' not in self.pack.config:
            return
        for inputFile in self.pack.config['shapes']:
            with open(inputFile) as f:
                self.generateFile(json.load(f))

    def generateFile(self, data):
        for key, value in data.items():
            self.writeFile(ResourceLocation(key), value)

    def writeFile(self, file: ResourceLocation, data: any):
        if self.exportFormat == 'yaml' and isinstance(data, str):
            file = self.pack.toAbsPath(self.dataPath, file, '.' + self.exportFormat)
            Path(file).parent.mkdir(parents=True, exist_ok=True)
            with open(file, 'w', encoding='utf-8') as f:
                f.write(data)
                self.count += 1
        else:
            super().writeFile(file, data)
