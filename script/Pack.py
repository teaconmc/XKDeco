import os
import tempfile
import shutil
from pathlib import Path

from CreativeTabProvider import CreativeTabProvider
from DataProvider import DataProvider
from ResourceLocation import ResourceLocation
from TranslationProvider import TranslationProvider


class Pack:
    def __init__(self, config):
        self.config = config
        self.providers = []
        self.tempDir = tempfile.mkdtemp()
        if 'json_pretty_print' not in self.config:
            self.config['json_pretty_print'] = False
        if 'translation_file_name' not in self.config:
            self.config['translation_file_name'] = '{}.json'
        if 'translation_dest' in self.config:
            self.config['translation_dest'] = self.config['translation_dest'].replace('%TEMP%', self.tempDir)
        self.creativeTabs = CreativeTabProvider(self)
        self.translations = TranslationProvider(self)
        print('Temp directory:', self.tempDir)

    def addProvider(self, provider: DataProvider):
        self.providers.append(provider)

    def finish(self):
        # self.providers.append(self.creativeTabs)
        self.providers.append(self.translations)
        for provider in self.providers:
            provider.generate()
            print('Generated', provider.count, 'files for', str(provider))
        self.providers.clear()
        dest = Path(self.config['dest'])
        if dest.exists():
            shutil.rmtree(dest)
        dest.mkdir(parents=True, exist_ok=True)
        for f in os.listdir(self.tempDir):
            shutil.move(os.path.join(self.tempDir, f), dest)
        shutil.rmtree(self.tempDir)

    def defaultResourceLocation(self, path: str) -> ResourceLocation:
        if ':' in path:
            return ResourceLocation(path)
        else:
            return ResourceLocation(self.config['namespace'], path)

    def toAbsPath(self, data_path: str, file: ResourceLocation, ext: str = '') -> str:
        return os.path.join(self.tempDir, data_path.format(file.namespace), file.path + ext)
