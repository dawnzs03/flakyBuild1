/*******************************************************************************
 *     ___                  _   ____  ____
 *    / _ \ _   _  ___  ___| |_|  _ \| __ )
 *   | | | | | | |/ _ \/ __| __| | | |  _ \
 *   | |_| | |_| |  __/\__ \ |_| |_| | |_) |
 *    \__\_\\__,_|\___||___/\__|____/|____/
 *
 *  Copyright (c) 2014-2019 Appsicle
 *  Copyright (c) 2019-2023 QuestDB
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 ******************************************************************************/

package io.questdb.griffin.engine.functions.long128;

import io.questdb.cairo.CairoConfiguration;
import io.questdb.cairo.sql.Function;
import io.questdb.cairo.sql.Record;
import io.questdb.griffin.FunctionFactory;
import io.questdb.griffin.SqlExecutionContext;
import io.questdb.griffin.engine.functions.BinaryFunction;
import io.questdb.griffin.engine.functions.Long128Function;
import io.questdb.std.IntList;
import io.questdb.std.Misc;
import io.questdb.std.ObjList;

public class LongsToLong128FunctionFactory implements FunctionFactory {
    @Override
    public String getSignature() {
        return "to_long128(LL)";
    }

    @Override
    public Function newInstance(
            int position,
            ObjList<Function> args,
            IntList argPositions,
            CairoConfiguration configuration,
            SqlExecutionContext sqlExecutionContext
    ) {
        final Function loLong = args.getQuick(0);
        final Function hiLong = args.getQuick(1);
        return new LongsToLong128Function(loLong, hiLong);
    }

    private static class LongsToLong128Function extends Long128Function implements BinaryFunction {
        private final Function hi;
        private final Function lo;

        public LongsToLong128Function(Function lo, Function hi) {
            this.lo = lo;
            this.hi = hi;
        }

        @Override
        public void close() {
            Misc.free(lo);
            Misc.free(hi);
        }

        @Override
        public Function getLeft() {
            return lo;
        }

        @Override
        public long getLong128Hi(Record rec) {
            return hi.getLong(rec);
        }

        @Override
        public long getLong128Lo(Record rec) {
            return lo.getLong(rec);
        }

        @Override
        public String getName() {
            return "to_long128";
        }

        @Override
        public Function getRight() {
            return hi;
        }

        @Override
        public boolean isReadThreadSafe() {
            return false;
        }
    }
}