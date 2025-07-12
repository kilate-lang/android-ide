package mo.kilate.ide.ui.activities.editor.components;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.google.android.material.color.MaterialColors;
import java.util.Arrays;
import java.util.List;
import mo.kilate.ide.R;
import mo.kilate.ide.databinding.LayoutFileNodeBinding;
import mo.kilate.ide.databinding.LayoutFilesDrawerBinding;
import mo.kilate.ide.io.File;
import mo.kilate.ide.utils.DesignUtil;
import mo.kilate.treeview.model.TreeNode;
import mo.kilate.treeview.view.AndroidTreeView;

public class FilesDrawerLayout extends LinearLayout {

  private final LayoutFilesDrawerBinding binding;
  private TreeNode.TreeNodeClickListener onFileClickListener;
  private TreeNode.TreeNodeLongClickListener onNodeLongClickListener;
  private File dir;

  public FilesDrawerLayout(final Context context) {
    this(context, null);
  }

  public FilesDrawerLayout(final Context context, final AttributeSet attrSet) {
    this(context, attrSet, -1);
  }

  public FilesDrawerLayout(final Context context, final AttributeSet attrSet, final int styleId) {
    super(context, attrSet, styleId);
    binding = LayoutFilesDrawerBinding.inflate(LayoutInflater.from(context), this, true);
  }

  private final View createView(final File dir) {
    final TreeNode nodes = createLazyFileNode(dir);
    final AndroidTreeView tree = new AndroidTreeView(getContext(), nodes);
    tree.setDefaultAnimation(true);
    tree.setUse2dScroll(true);
    tree.setDefaultContainerStyle(R.style.Widget_KilateIDE_TreeNodeStyle, true);
    tree.setDefaultNodeLongClickListener(onNodeLongClickListener);
    tree.setDefaultNodeClickListener(onFileClickListener);
    return tree.getView();
  }

  public final FilesDrawerLayout setDirectory(final File dir) {
    this.dir = dir;
    binding.treeContainer.removeAllViews();
    final View treeView = createView(dir);
    binding.treeContainer.addView(treeView);
    return this;
  }

  public final FilesDrawerLayout setOnFileClickListener(
      final TreeNode.TreeNodeClickListener onFileClickListener) {
    this.onFileClickListener = onFileClickListener;
    return this;
  }

  public final FilesDrawerLayout setOnNodeLongClickListener(
      final TreeNode.TreeNodeLongClickListener onNodeLongClickListener) {
    this.onNodeLongClickListener = onNodeLongClickListener;
    return this;
  }

  public final FilesDrawerLayout refresh() {
    return setDirectory(dir);
  }

  private final TreeNode createFileTree(File dir) {
    final FileNode nodeValue = new FileNode(dir);
    final TreeNode node = new TreeNode(nodeValue).setViewHolder(new TreeNodeHolder(getContext()));

    if (dir.isDirectory()) {
      final List<File> files = File.toFiles(Arrays.asList(dir.listFiles()));
      if (files != null) {
        for (File file : files) {
          node.addChild(createFileTree(file));
        }
      }
    }
    return node;
  }

  private final TreeNode createLazyFileNode(final File dir) {
    final FileNode nodeValue = new FileNode(dir);
    final TreeNode node = new TreeNode(nodeValue).setViewHolder(new TreeNodeHolder(getContext()));

    if (dir.isDirectory()) {
      final List<File> files = File.toFiles(Arrays.asList(dir.listFiles()));
      if (files != null) {
        for (final File file : files) {
          TreeNode childNode =
              new TreeNode(new FileNode(file)).setViewHolder(new TreeNodeHolder(getContext()));

          if (file.isDirectory()) {
            childNode.setClickListener(
                (n, v) -> {
                  ((ImageView) n.getViewHolder().getView().findViewById(R.id.node_expand))
                      .setImageResource(
                          (n.isExpanded()) ? R.drawable.ic_expand : R.drawable.ic_collapse);
                  if (n.getChildren().isEmpty()) {
                    final File currentDir = ((FileNode) n.getValue()).getAbsolute();
                    final List<File> subFiles = File.toFiles(Arrays.asList(currentDir.listFiles()));
                    if (subFiles != null) {
                      for (final File subFile : subFiles) {
                        n.addChild(createLazyFileNode(subFile));
                      }
                    }
                  }
                });
          } else {
            childNode.setClickListener(onFileClickListener);
          }
          node.addChild(childNode);
        }
      }
    }
    return node;
  }

  public final FilesDrawerLayout collapseAll(final TreeNode node) {
    for (final TreeNode child : node.getChildren()) {
      child.setExpanded(false);
      collapseAll(child);
    }
    return this;
  }

  public static class TreeNodeHolder extends TreeNode.BaseNodeViewHolder<FileNode> {

    private final Context ctx;

    public TreeNodeHolder(final Context ctx) {
      super(ctx);
      this.ctx = ctx;
    }

    private final Drawable createDrawableFromStr(final View v, final String str, final int sizePx) {
      return DesignUtil.createDrawableFromStr(
          context,
          str,
          sizePx,
          MaterialColors.getColor(v, com.google.android.material.R.attr.colorPrimary),
          Typeface.MONOSPACE);
    }

    @Override
    public View createNodeView(TreeNode node, FileNode value) {
      final LayoutFileNodeBinding binding =
          LayoutFileNodeBinding.inflate(LayoutInflater.from(context));

      binding.nodeValue.setText(value.getName().isEmpty() ? "/" : value.getName());

      if (node.isExpanded()) {
        binding.nodeExpand.setImageResource(R.drawable.ic_collapse);
      } else {
        binding.nodeExpand.setImageResource(R.drawable.ic_expand);
      }

      if (value.isDirectory()) {
        binding.nodeIcon.setImageResource(R.drawable.ic_mtrl_folder);
      } else {
        binding.nodeExpand.setVisibility(View.GONE);
        final String extension = value.getName().substring(value.getName().lastIndexOf(".") + 1);
        switch (extension) {
          case "klt" -> {
            binding.nodeIcon.setImageDrawable(createDrawableFromStr(binding.getRoot(), "K", 100));
          }
          case "c", "h" -> {
            binding.nodeIcon.setImageDrawable(createDrawableFromStr(binding.getRoot(), "C", 100));
          }
          case "cpp", "hpp" -> {
            binding.nodeIcon.setImageDrawable(createDrawableFromStr(binding.getRoot(), "C++", 30));
          }
          default -> {
            binding.nodeIcon.setImageResource(R.drawable.ic_mtrl_file);
          }
        }
      }
      return binding.getRoot();
    }
  }
}
