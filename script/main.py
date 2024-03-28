import json
import os
from pathlib import Path

from TableDataProvider import TableDataProvider
from BlockDefinitionProvider import BlockDefinitionProvider
from MaterialProvider import MaterialProvider
from Pack import Pack


def main():
    # check if loader_config.json exists
    if not Path('loader_config.json').exists():
        if Path('script/loader_config.json').exists():
            os.chdir('script')
        else:
            print('loader_config.json not found')
            return

    with open('loader_config.json', encoding='utf-8') as f:
        config = json.load(f)
    pack = Pack(config)
    pack.addProvider(MaterialProvider(pack))
    pack.addProvider(TableDataProvider(pack, 'assets/{}/kiwi/block_template', 'block_templates'))
    pack.addProvider(BlockDefinitionProvider(pack))
    pack.finish()


if __name__ == '__main__':
    main()
