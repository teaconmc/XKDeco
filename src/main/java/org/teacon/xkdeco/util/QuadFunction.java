package org.teacon.xkdeco.util;

@FunctionalInterface
public interface QuadFunction<P1, P2, P3, P4, R> {
	R apply(P1 pP1, P2 pP2, P3 pP3, P4 pP4);
}
