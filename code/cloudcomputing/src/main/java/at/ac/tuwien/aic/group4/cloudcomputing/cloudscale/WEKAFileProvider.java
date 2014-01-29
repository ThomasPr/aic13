package at.ac.tuwien.aic.group4.cloudcomputing.cloudscale;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.infosys.cloudscale.annotations.FileDependency.DependentFile;
import at.ac.tuwien.infosys.cloudscale.annotations.FileDependency.IFileDependencyProvider;

public class WEKAFileProvider implements IFileDependencyProvider {

	@Override
	public DependentFile[] getDependentFiles() {
		
		List<DependentFile> dependentFiles = new ArrayList<>();
		
		for(File file : new File("files").listFiles())
			if(file.isFile())
				dependentFiles.add(new DependentFile(file.getPath()));
		
		return dependentFiles.toArray(new DependentFile[0]);
	}
}
