

// Copyright 2008-2009 Emilio Lopez-Gabeiras
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License
//


package apb.utils;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import apb.Environment;
//
// User: emilio
// Date: Dec 4, 2008
// Time: 4:40:10 PM

public class SimpleFormatter
    extends Formatter
{
    //~ Instance fields ......................................................................................

    private Environment env;
    private boolean     eol = true;
    private static final int HEADER_LENGTH = 30;

    //~ Constructors .........................................................................................

    public SimpleFormatter(Environment env)
    {
        this.env = env;
    }

    //~ Methods ..............................................................................................

    public String format(LogRecord record)
    {
        StringBuilder result = new StringBuilder();

        if (eol) {
            appendHeader(result);
            eol = false;
        }

        String msg = formatMsg(record);

        if (!msg.isEmpty()) {
            int p;

            while ((p = msg.indexOf('\n')) >= 0) {
                result.append(msg.substring(0, ++p));
                msg = msg.substring(p);
                eol = !msg.isEmpty();

                if (eol) {
                    appendHeader(result);
                    eol = false;
                }
            }

            if (msg.isEmpty()) {
                eol = true;
            }
            else {
                result.append(msg);
            }
        }

        return result.toString();
    }

    private String formatMsg(LogRecord record)
    {
        String         msg = record.getMessage();
        final Object[] pars = record.getParameters();

        if (pars.length > 0) {
            msg = String.format(msg, pars);
        }

        return msg;
    }

    protected void appendHeader(StringBuilder result)
    {
        if (env.getCurrent() != null) {
            int n = result.length();
            result.append("[");
            result.append(env.getCurrentName());

            if (env.getCurrentCommand() != null) {
                result.append(".");
                result.append(env.getCurrentCommand().getQName());
            }
            if (result.length()-n > HEADER_LENGTH)
                result.setLength(n+HEADER_LENGTH);

            result.append("]");
            n = result.length() - n;

            for (int i = HEADER_LENGTH+1 - n; i >= 0; i--) {
                result.append(' ');
            }
        }
    }
}
