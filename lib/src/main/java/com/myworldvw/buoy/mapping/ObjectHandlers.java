package com.myworldvw.buoy.mapping;

import java.util.List;

public record ObjectHandlers<T>(Class<T> type, List<StructMappingHandler<T>> structFieldHandlers, List<FunctionHandler<T>> functionHandlers, List<GlobalHandler<T>> globalHandlers) {}
