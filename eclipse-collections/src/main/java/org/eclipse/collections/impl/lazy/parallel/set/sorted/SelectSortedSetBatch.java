/*
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.lazy.parallel.set.sorted;

import org.eclipse.collections.api.annotation.Beta;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.impl.block.procedure.IfProcedure;
import org.eclipse.collections.impl.lazy.parallel.AbstractBatch;
import org.eclipse.collections.impl.lazy.parallel.list.ListBatch;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;

@Beta
public class SelectSortedSetBatch<T> extends AbstractBatch<T> implements SortedSetBatch<T>
{
    private final SortedSetBatch<T> sortedSetBatch;
    private final Predicate<? super T> predicate;

    public SelectSortedSetBatch(SortedSetBatch<T> sortedSetBatch, Predicate<? super T> predicate)
    {
        this.sortedSetBatch = sortedSetBatch;
        this.predicate = predicate;
    }

    public void forEach(Procedure<? super T> procedure)
    {
        this.sortedSetBatch.forEach(new IfProcedure<T>(this.predicate, procedure));
    }

    /*
    public SetBatch<T> select(Predicate<? super T> predicate)
    {
        return new SelectSetBatch<T>(this.setBatch, Predicates.and(this.predicate, predicate));
    }
    */

    public SortedSetBatch<T> select(Predicate<? super T> predicate)
    {
        return new SelectSortedSetBatch<T>(this, predicate);
    }

    public <V> ListBatch<V> collect(Function<? super T, ? extends V> function)
    {
        return new CollectSortedSetBatch<T, V>(this, function);
    }

    public <V> ListBatch<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return new FlatCollectSortedSetBatch<T, V>(this, function);
    }

    public SortedSetBatch<T> distinct(ConcurrentHashMap<T, Boolean> distinct)
    {
        return this;
    }
}
