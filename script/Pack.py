import os
import shutil
import tempfile
from pathlib import Path
from zipfile import ZipFile

import titlecase

import PackVersion
from DataProvider import DataProvider
from ResourceLocation import ResourceLocation


class Pack:
    def __init__(self, config):
        self.config = config
        self.providers = {}
        self.tempDir = tempfile.mkdtemp()
        if 'json_pretty_print' not in self.config:
            self.config['json_pretty_print'] = False
        if 'translation_file_name' not in self.config:
            self.config['translation_file_name'] = '{}.json'
        if 'export_format' not in self.config:
            self.config['export_format'] = 'yaml'
        else:
            self.config['export_format'] = self.config['export_format'].lower()
        if 'pack_version' not in self.config:
            packpack_version = '1.20'
        else:
            packpack_version = self.config['pack_version']
        self.packVersion = PackVersion.get(packpack_version)
        if 'translation_dest' in self.config:
            self.config['translation_dest'] = self.config['translation_dest'].replace('%TEMP%', self.tempDir)
        self.includes = []
        if 'includes' in self.config:
            self.includes = self.config['includes']
        for include in self.includes:
            path = Path(include)
            if not path.is_dir():
                raise ValueError('Include path is not a directory: ' + path.absolute().as_posix())
        print('Temp directory:', self.tempDir)
        titlecase.set_small_word_list('|'.join(
            ['of', 'the', 'and', 'in', 'on', 'at', 'for', 'to', 'with', 'as', 'by', 'or', 'an', 'a', 'but', 'nor', 'up', 'so', 'yet', 'if',
             'via', 'without']))

    def addProvider(self, provider: DataProvider):
        self.providers[provider.identifier] = provider

    def finish(self):
        for provider in self.providers.values():
            if not provider.canGenerate():
                continue
            provider.generate()
            print('Generated', provider.count, 'files for', str(provider))
        self.providers.clear()

        for include in self.includes:
            shutil.copytree(include, self.tempDir, dirs_exist_ok=True)

        dest = Path(self.config['dest'])
        if dest.name.lower().endswith('.zip'):
            if not Path(os.path.join(self.tempDir, 'pack.mcmeta')).exists():
                raise ValueError('pack.mcmeta not found in dest package')
            dest.parent.mkdir(parents=True, exist_ok=True)
            with ZipFile(dest, 'w') as zip:
                for root, _, files in os.walk(self.tempDir):
                    for file in files:
                        zip.write(os.path.join(root, file), os.path.relpath(os.path.join(root, file), self.tempDir))
        else:
            if dest.exists():
                shutil.rmtree(dest)
            dest.mkdir(parents=True, exist_ok=True)
            for f in os.listdir(self.tempDir):
                shutil.move(os.path.join(self.tempDir, f), dest)
        shutil.rmtree(self.tempDir)
        print('Finished building pack:', dest.resolve().as_uri())

    def defaultResourceLocation(self, path: str) -> ResourceLocation:
        path = path.strip()
        if ':' in path:
            return ResourceLocation(path)
        else:
            return ResourceLocation(self.config['namespace'], path)

    def toAbsPath(self, data_provider: DataProvider, file: ResourceLocation, ext: str = None) -> str:
        if ext is None:
            ext = '.' + data_provider.exportFormat
        return os.path.join(self.tempDir, data_provider.dataPath.format(file.namespace, file.path), file.path + ext)
