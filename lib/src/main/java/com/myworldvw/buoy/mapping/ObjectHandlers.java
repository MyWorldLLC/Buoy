package com.myworldvw.buoy.mapping;

import java.util.List;

public record ObjectHandlers<T>(Class<T> type, List<MappingHandler<T>> structFieldHandlers, List<FunctionHandler<T>> functionHandlers) {}
