import os
import shutil
from pathlib import Path

import yaml

from BlockDefinitionProvider import BlockDefinitionProvider
from BlockTemplateProvider import BlockTemplateProvider
from CreativeTabProvider import CreativeTabProvider
from MaterialProvider import MaterialProvider
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

    with open(configPath, encoding='utf-8') as f:
        config = yaml.safe_load(f)
    pack = Pack(config)
    pack.addProvider(MaterialProvider(pack))
    pack.addProvider(BlockTemplateProvider(pack))
    pack.addProvider(BlockDefinitionProvider(pack))
    pack.addProvider(CreativeTabProvider(pack))
    pack.addProvider(TranslationProvider(pack))
    pack.addProvider(TagsProvider(pack, 'block', 'blocks'))
    pack.addProvider(ShapeProvider(pack))
    pack.finish()


if __name__ == '__main__':
    main()
