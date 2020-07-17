package io.github.lmikoto;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.refactoring.RefactoringFactory;
import com.intellij.refactoring.RenameRefactoring;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.lang3.StringUtils;

public class BatchRename extends AnAction {

    private AnAction myTargetAction;

    private RenameBean renameBean = new RenameBean();

    @Override
    public void actionPerformed(AnActionEvent event) {
        DataContext dataContext = event.getDataContext();
        Project project =event.getProject();
        String cmdName = getCmdName();

        CommandProcessor commandProcessor = CommandProcessor.getInstance();
        Runnable command = () -> {
            ApplicationManager.getApplication().runWriteAction(()->{
                doAction(project,dataContext,event);
            });
        };
        commandProcessor.executeCommand(project, command, cmdName, null);
    }

    private void doAction(Project project, DataContext dataContext,AnActionEvent event){
        PsiManager psiManager = PsiManager.getInstance(project);
        VirtualFile[] virtualFiles = DataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext);
        listFiles(dataContext,project,psiManager,virtualFiles,event);
    }

    private void listFiles (DataContext dataContext, Project project, PsiManager psiManager, VirtualFile[] virtualFiles, AnActionEvent event){
        if(virtualFiles != null && virtualFiles.length > 0){
            if (virtualFiles.length == 1 && !virtualFiles[0].isDirectory()) {
                PsiFile psiFile = psiManager.findFile(virtualFiles[0]);
                this.actionOn(dataContext, project, psiManager, psiFile, event);
            } else if (this.containsFile(virtualFiles)) {
                BatchRenameForm fileRenameForm = new BatchRenameForm(renameBean);
                fileRenameForm.pack();
                fileRenameForm.setLocationRelativeTo(null);
                fileRenameForm.setVisible(true);
                if (renameBean != null) {
                    this.actionOn(dataContext, project, psiManager, virtualFiles, event);
                }
            }
        }
    }

    private boolean containsFile(VirtualFile[] virtualFiles) {
        boolean containsFile = false;
        for(int i = 0; i < virtualFiles.length && !containsFile; ++i) {
            VirtualFile virtualFile = virtualFiles[i];
            if (virtualFile.isDirectory()) {
                containsFile = this.containsFile(virtualFile.getChildren());
            } else {
                containsFile = true;
            }
        }
        return containsFile;
    }

    private void actionOn(DataContext dataContext, Project project, PsiManager psiManager, PsiFile psiFile, AnActionEvent event) {
        ActionManager actionManager = ActionManager.getInstance();
        DefaultActionGroup actionGroup = (DefaultActionGroup)actionManager.getAction("RefactoringMenu");
        if (actionGroup != null) {
            this.myTargetAction = actionManager.getAction("RenameElement");
            if (this.myTargetAction != null) {
                this.myTargetAction.actionPerformed(event);
            }
        }
    }

    private void actionOn(DataContext dataContext, Project project, PsiManager psiManager, VirtualFile[] virtualFiles, AnActionEvent event){
        for(VirtualFile virtualFile : virtualFiles){
            if (virtualFile.isDirectory()) {
                this.actionOn(dataContext, project, psiManager, virtualFile.getChildren(), event);
            }else {
                PsiFile psiFile = psiManager.findFile(virtualFile);
                boolean valid = true;
//                if (this.fileRenameBean.hasFileMask() && ValidatorUtil.isNotBlank(this.fileRenameBean.getFileMaskPattern())) {
//                    RegExpValidator regExpValidator = new RegExpValidator(this.convertToRegex(this.fileRenameBean.getFileMaskPattern()));
//                    valid = regExpValidator.value(psiFile.getName());
//                }

                if (valid) {
                    try {
                        this.renameFile(project, psiFile);
                    } catch (IncorrectOperationException e) {
                        e.printStackTrace();;
                    }
                    PsiDocumentManager.getInstance(project).commitAllDocuments();
                    EditorFactory.getInstance().refreshAllEditors();
                }
            }
        }
    }

    private void renameFile(Project project, PsiFile psiFile) throws IncorrectOperationException {
        String oldFileName = psiFile.getName();
        String newName = StringUtils.replace(oldFileName,renameBean.getTextToFind(),renameBean.getReplaceWith());

        RefactoringFactory refactoringFactory = RefactoringFactory.getInstance(project);
        RenameRefactoring renameRefactoring = refactoringFactory.createRename(psiFile, newName);

        if (psiFile instanceof PsiJavaFile) {
            String newClassName = StringUtils.removeEnd(newName, ".java");
            String oldClassName = StringUtils.removeEnd(oldFileName, ".java");
            PsiJavaFile psiJavaFile = (PsiJavaFile)psiFile;
            PsiClass[] psiClasses = psiJavaFile.getClasses();
            for(PsiClass psiClass: psiClasses) {
                if (psiClass.getName().equals(oldClassName)) {
                    renameRefactoring.addElement(psiClass, newClassName);
                }
            }
        }

        renameRefactoring.setSearchInComments(renameBean.getSearchInCommentsAndStrings());
        renameRefactoring.setSearchInNonJavaFiles(renameBean.getSearchForTextOccurrences());
        renameRefactoring.run();
    }

    private void actionOn(DataContext dataContext, Project project, PsiManager psiManager, PsiJavaFile psiJavaFile, AnActionEvent event){
        PsiClass[] psiClasses = psiJavaFile.getClasses();
        for (PsiClass psiClass : psiClasses) {
            actionOn(dataContext, project, psiManager, psiClass, event);
        }
    }

    private void actionOn(DataContext dataContext, Project project, PsiManager psiManager, PsiClass psiClass, AnActionEvent event){
        PsiMethod[] methods = psiClass.getMethods();
        for(int i =0;i<methods.length;i++){
            actionOn(dataContext,project,psiManager,methods[i],event);
        }
        PsiClass[] inner = psiClass.getAllInnerClasses();
        for(int i =0;i<inner.length;i++){
            actionOn(dataContext,project,psiManager,inner[i],event);
        }
    }

    private void actionOn(DataContext dataContext, Project project, PsiManager psiManager, PsiMethod method, AnActionEvent event) {
    }

    private String getCmdName () {
        String cmdName = getTemplatePresentation().getText();
        if(StringUtils.isBlank(cmdName)){
            cmdName = "";
        }
        return cmdName;
    }
}
