/**
 * Copyright (C) 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ninja.mustache.exception;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import ninja.mustache.utils.MustacheConstant;
import ninja.utils.NinjaProperties;
import ninja.utils.ResponseStreams;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * A general exception handler for exceptions. Outputs a readable error in test
 * dev mode. - Outputs a error message with relevant status code in production.
 * 
 * @author ra, sojin, kpacha
 */
@Singleton
public class NinjaExceptionHandler {

    private final NinjaProperties ninjaProperties;
    private final Logger logger;

    @Inject
    public NinjaExceptionHandler(Logger logger, NinjaProperties ninjaProperties) {
	this.logger = logger;
	this.ninjaProperties = ninjaProperties;
    }

    public void handleException(Exception te, String response,
	    ResponseStreams outStream) {
	try {
	    Writer out = outStream.getWriter();
	    PrintWriter pw = (out instanceof PrintWriter) ? (PrintWriter) out
		    : new PrintWriter(out);

	    // TODO render with proper http status code.
	    if (!ninjaProperties.isDev()) {

		if (response == null
			|| response
				.endsWith(MustacheConstant.DEFAULT_EXTENSION)) {
		    response = "Server error!";
		}

		pw.println(response);

		logger.log(
			Level.SEVERE,
			"Templating error. This should not happen in production",
			te);
	    } else {
		// print out full stacktrace if we are in test or dev mode
		pw.println("<!-- Mustache Template ERROR MESSAGE STARTS HERE -->"
			+ "<script language=javascript>//\"></script>"
			+ "<script language=javascript>//\'></script>"
			+ "<script language=javascript>//\"></script>"
			+ "<script language=javascript>//\'></script>"
			+ "</title></xmp></script></noscript></style></object>"
			+ "</head></pre></table>"
			+ "</form></table></table></table></a></u></i></b>"
			+ "<div align=left "
			+ "style='background-color:#FFFF00; color:#FF0000; "
			+ "display:block; border-top:double; padding:2pt; "
			+ "font-size:medium; font-family:Arial,sans-serif; "
			+ "font-style: normal; font-variant: normal; "
			+ "font-weight: normal; text-decoration: none; "
			+ "text-transform: none'>"
			+ "<b style='font-size:medium'>Mustache template error!</b>"
			+ "<pre><xmp>");
		te.printStackTrace(pw);
		pw.println("</xmp></pre></div></html>");
		logger.log(Level.SEVERE, "Templating error.", te);
	    }

	    pw.flush();
	    pw.close();

	} catch (IOException e) {
	    logger.log(Level.SEVERE, "Error while handling error.", e);
	}
    }
}