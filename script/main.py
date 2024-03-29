import json
import os
from pathlib import Path

from TableDataProvider import TableDataProvider
from BlockDefinitionProvider import BlockDefinitionProvider
from MaterialProvider import MaterialProvider
from Pack import Pack


def main():
    configPath = 'loader_config.json'
    if len(os.sys.argv) > 1:
        configPath = os.sys.argv[1]
    if not Path(configPath).exists():
        if Path('script/' + configPath).exists():
            os.chdir('script')
        else:
            print(configPath + ' not found')
            return

    with open(configPath, encoding='utf-8') as f:
        config = json.load(f)
    pack = Pack(config)
    pack.addProvider(MaterialProvider(pack))
    pack.addProvider(TableDataProvider(pack, 'assets/{}/kiwi/template/block', 'block_templates'))
    pack.addProvider(BlockDefinitionProvider(pack))
    pack.finish()


if __name__ == '__main__':
    main()
