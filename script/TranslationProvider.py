from script.DataProvider import DataProvider
from script.ResourceLocation import ResourceLocation

import os
import json
from pathlib import Path


class TranslationProvider(DataProvider):
    def __init__(self, pack):
        super().__init__(pack, 'assets/{}/lang')
        self.data = {}
        self.prettyPrint = True

    def putTranslation(self, lang: str, key: str, value: str):
        if lang not in self.data:
            self.data[lang] = {}
        self.data[lang][key] = value

    def generate(self):
        format: str = self.pack.config['translation_file_name']
        assert format.endswith('.json'), 'Translation file name must end with .json'
        format = format[:-5]
        for lang, translations in self.data.items():
            self.writeJson(self.pack.defaultResourceLocation(format.format(lang)), translations)

    def writeJson(self, file, data: dict):
        if 'translation_dest' in self.pack.config:
            file = os.path.join(self.pack.config['translation_dest'], file.path + '.json')
        else:
            file = self.pack.toAbsPath(self.dataPath, file, '.json')
        Path(file).parent.mkdir(parents=True, exist_ok=True)
        with open(file, 'w', encoding='utf-8') as f:
            json.dump(data, f, indent=2 if self.prettyPrint else None, ensure_ascii=False)
            self.count += 1
