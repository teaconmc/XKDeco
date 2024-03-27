import json

from TableDataProvider import TableDataProvider
from BlockDefinitionProvider import BlockDefinitionProvider
from MaterialProvider import MaterialProvider
from Pack import Pack


def main():
    with open('loader_config.json', encoding='utf-8') as f:
        config = json.load(f)
    pack = Pack(config)
    pack.addProvider(MaterialProvider(pack))
    pack.addProvider(TableDataProvider(pack, 'assets/{}/kiwi/block_template', 'block_templates'))
    pack.addProvider(BlockDefinitionProvider(pack))
    pack.finish()


if __name__ == '__main__':
    main()
