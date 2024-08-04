package org.teacon.xkdeco.init;

import org.teacon.xkdeco.util.CommonProxy;

import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiModule;

@KiwiModule
public class CoreModule extends AbstractModule {
	@Override
	protected void preInit() {
		CommonProxy.initCodecs();
	}
}
