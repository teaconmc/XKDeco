import os
import shutil
from pathlib import Path

import yaml

import Download
from BlockDefinitionProvider import BlockDefinitionProvider
from BlockFamilyProvider import BlockFamilyProvider
from BlockTemplateProvider import BlockTemplateProvider
from CreativeTabProvider import CreativeTabProvider
from GlassTypeProvider import GlassTypeProvider
from ItemDefinitionProvider import ItemDefinitionProvider
from ItemTemplateProvider import ItemTemplateProvider
from MaterialProvider import MaterialProvider
from MetadataProvider import MetadataProvider
from Pack import Pack
from ShapeProvider import ShapeProvider
from TagsProvider import TagsProvider
from TranslationProvider import TranslationProvider


def main():
    configPath = 'config.yaml'
    if len(os.sys.argv) > 1:
        configPath = os.sys.argv[1]
    if not Path(configPath).exists():
        if Path('script/' + configPath).exists():
            os.chdir('script')
        else:
            print(configPath + ' not found')
            return

    with open(configPath, encoding='utf-8') as f:
        config = yaml.safe_load(f)

    if 'namespace' not in config:
        raise ValueError('namespace not found in config')

    if 'google_sheets_id' in config:
        Download.downloadFileFromGoogleSheets(config['google_sheets_id'], '../' + config['namespace'] + '.xlsx')

    if Path('move_files.yaml').exists():
        with open('move_files.yaml', encoding='utf-8') as f:
            moveFiles = yaml.safe_load(f).get(configPath, {}).get('move', [])
        for moveFile in moveFiles:
            src = Path(moveFile)
            if not src.exists():
                continue
            dest = Path(src.name)
            shutil.move(src, dest)
            print('Moved', src.absolute(), 'to', dest.absolute())

    pack = Pack(config)

    pack.addProvider(MaterialProvider(pack))
    pack.addProvider(GlassTypeProvider(pack))
    pack.addProvider(BlockTemplateProvider(pack))
    pack.addProvider(BlockDefinitionProvider(pack))
    pack.addProvider(TagsProvider(pack, 'block'))
    pack.addProvider(ShapeProvider(pack))

    pack.addProvider(ItemTemplateProvider(pack))
    pack.addProvider(ItemDefinitionProvider(pack))
    pack.addProvider(TagsProvider(pack, 'item'))

    pack.addProvider(BlockFamilyProvider(pack))
    pack.addProvider(CreativeTabProvider(pack))
    generateMetadata = True
    if 'generate_metadata' in config:
        generateMetadata = config['generate_metadata']
    if generateMetadata:
        pack.addProvider(MetadataProvider(pack))
    pack.addProvider(TranslationProvider(pack))

    pack.finish()


if __name__ == '__main__':
    main()
