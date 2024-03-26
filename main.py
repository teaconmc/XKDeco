import json

from script.BlockDefinitionProvider import BlockDefinitionProvider
from script.MaterialProvider import MaterialProvider
from script.Pack import Pack


def main():
    with open('loader_config.json', encoding='utf-8') as f:
        config = json.load(f)
    pack = Pack(config)
    pack.addProvider(MaterialProvider(pack))
    pack.addProvider(BlockDefinitionProvider(pack))
    pack.finish()


if __name__ == '__main__':
    main()
