package net.avh4.test.courtreporter;

import net.avh4.test.courtreporter.representation.ObjectRep;
import net.avh4.test.courtreporter.representation.Rep;

public interface RecordingReporter {
    void methodCall(ObjectRep objectId, String methodName, Rep returnValue, Rep... args);
}
