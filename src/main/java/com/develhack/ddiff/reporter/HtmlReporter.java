package com.develhack.ddiff.reporter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.develhack.ddiff.Diff;
import com.develhack.ddiff.Reporter;
import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class HtmlReporter implements Reporter {

    private final DiffRowGenerator generator = DiffRowGenerator.create()
            .showInlineDiffs(true)
            .oldTag(f -> f ? "<span class=\"old\">" : "</span>")
            .newTag(f -> f ? "<span class=\"new\">" : "</span>")
            .build();

    @Override
    public String getFormat() {
        return "html";
    }

    @Override
    public void report(Path originalRoot, Path revisedRoot, List<Diff> diffs, OutputStream os) throws IOException {

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setClassForTemplateLoading(getClass(), "");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setOutputEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        Template template = cfg.getTemplate("template.ftl");

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("reporter", this);
        dataModel.put("originalRoot", originalRoot);
        dataModel.put("revisedRoot", revisedRoot);
        dataModel.put("diffs", diffs);

        try (Writer out = new OutputStreamWriter(os, "UTF-8")) {

            template.process(dataModel, out);

        } catch (TemplateException e) {
            throw new IOException(e);
        }
    }

    public List<DiffRow> getDiffRows(Diff diff) {
        return generator.generateDiffRows(diff.getOriginalLines(), diff.getPatch());
    }
}
