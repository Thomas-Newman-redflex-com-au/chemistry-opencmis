/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.chemistry.opencmis.workbench.details;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.workbench.actions.CancelCheckOutPanel;
import org.apache.chemistry.opencmis.workbench.actions.CheckInPanel;
import org.apache.chemistry.opencmis.workbench.actions.CheckOutPanel;
import org.apache.chemistry.opencmis.workbench.actions.DeleteContentStreamPanel;
import org.apache.chemistry.opencmis.workbench.actions.DeletePanel;
import org.apache.chemistry.opencmis.workbench.actions.DeleteTreePanel;
import org.apache.chemistry.opencmis.workbench.actions.MovePanel;
import org.apache.chemistry.opencmis.workbench.actions.SetContentStreamPanel;
import org.apache.chemistry.opencmis.workbench.model.ClientModel;
import org.apache.chemistry.opencmis.workbench.model.ClientModelEvent;
import org.apache.chemistry.opencmis.workbench.model.ObjectListener;

public class ActionsPanel extends JPanel implements ObjectListener {

	private static final long serialVersionUID = 1L;

	private ClientModel model;

	private DeletePanel deletePanel;
	private DeleteTreePanel deleteTreePanel;
	private MovePanel movePanel;
	private CheckOutPanel checkOutPanel;
	private CancelCheckOutPanel cancelCheckOutPanel;
	private CheckInPanel checkInPanel;
	private SetContentStreamPanel setContentStreamPanel;
	private DeleteContentStreamPanel deleteContentStreamPanel;

	public ActionsPanel(ClientModel model) {
		super();

		this.model = model;
		model.addObjectListener(this);

		createGUI();
	}

	public void objectLoaded(ClientModelEvent event) {
		CmisObject object = model.getCurrentObject();

		deletePanel.setObject(object);
		deletePanel.setVisible(deletePanel.isAllowed());

		deleteTreePanel.setObject(object);
		deleteTreePanel.setVisible(deleteTreePanel.isAllowed());

		movePanel.setObject(object);
		movePanel.setVisible(movePanel.isAllowed());

		checkOutPanel.setObject(object);
		checkOutPanel.setVisible(checkOutPanel.isAllowed());

		cancelCheckOutPanel.setObject(object);
		cancelCheckOutPanel.setVisible(cancelCheckOutPanel.isAllowed());

		checkInPanel.setObject(object);
		checkInPanel.setVisible(checkInPanel.isAllowed());

		setContentStreamPanel.setObject(object);
		setContentStreamPanel.setVisible(setContentStreamPanel.isAllowed());

		deleteContentStreamPanel.setObject(object);
		deleteContentStreamPanel.setVisible(deleteContentStreamPanel
				.isAllowed());
	}

	private void createGUI() {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		deletePanel = new DeletePanel(model);
		add(deletePanel);

		deleteTreePanel = new DeleteTreePanel(model);
		add(deleteTreePanel);

		movePanel = new MovePanel(model);
		add(movePanel);

		checkOutPanel = new CheckOutPanel(model);
		add(checkOutPanel);

		cancelCheckOutPanel = new CancelCheckOutPanel(model);
		add(cancelCheckOutPanel);

		checkInPanel = new CheckInPanel(model);
		add(checkInPanel);

		setContentStreamPanel = new SetContentStreamPanel(model);
		add(setContentStreamPanel);

		deleteContentStreamPanel = new DeleteContentStreamPanel(model);
		add(deleteContentStreamPanel);
	}
}