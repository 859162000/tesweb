package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.dc.tes.ui.client.IComponent;
import com.dc.tes.ui.client.IComponentAsync;
import com.dc.tes.ui.client.MainPage;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.RequireTextField;
import com.dc.tes.ui.client.enums.ComponentEnum;
import com.dc.tes.ui.client.enums.CsType;
import com.dc.tes.ui.client.enums.MsgType;
import com.dc.tes.ui.client.model.GWTAdapter;
import com.dc.tes.ui.client.model.GWTChannel;
import com.dc.tes.ui.client.model.GWTComponent;
import com.dc.tes.ui.client.model.GWTMsgType;
import com.dc.tes.ui.client.model.GWTRecCode;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.dnd.DragSource;
import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class TesEnvBuildPage extends BasePage {

	IComponentAsync service = null;
	ContentPanel recCodePanel = null;
	
	Grid<GWTChannel> clientAdapterGrid = null;
	Grid<GWTChannel> serverAdapterGrid = null;
	
	ContentPanel clientAdapterContainer = new ContentPanel();
	ContentPanel serverAdapterContainer = new ContentPanel();
	ContentPanel recCodeContainer = new ContentPanel();
	ContentPanel packContainer = new ContentPanel();
	ContentPanel unpackContainer = new ContentPanel();
	ContentPanel mainPanel = null;
	LabelField lablePath = null;
	ComboBox<GWTChannel> defChannel = null;

	String currSysId = "";
	ListStore<GWTChannel> defChannelStore = new ListStore<GWTChannel>();
	ListStore<GWTChannel> sendChannelStore = new ListStore<GWTChannel>();
	ListStore<GWTChannel> receChannelStore = new ListStore<GWTChannel>();
	
	public TesEnvBuildPage(){
		
		currSysId = this.GetSystemID();
		service = ServiceHelper.GetDynamicService(ComponentBasePage.serviceName, IComponent.class);
		
		service.GetComponentListByType(ComponentEnum.Adapter, new AsyncCallback<List<GWTComponent>>(){

			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert("数据加载失败", "适配器组件列表加载失败", null);
			}

			@Override
			public void onSuccess(List<GWTComponent> result) {
				ReBindComponentPanel(clientAdapterContainer, result, true);
				ReBindComponentPanel(serverAdapterContainer, result, false);
			}
		});
		
		service.GetComponentListByType(ComponentEnum.MsgType, new AsyncCallback<List<GWTComponent>>(){

			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert("数据加载失败", "报文组件列表加载失败", null);
			}

			@Override
			public void onSuccess(List<GWTComponent> result) {
				ReBindComponentPanel(packContainer, result, true);
				ReBindComponentPanel(unpackContainer, result, false);
			}
		});
		
		service.GetComponentListByType(ComponentEnum.RecCode, new AsyncCallback<List<GWTComponent>>(){

			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert("数据加载失败", "交易识别组件列表加载失败", null);
			}

			@Override
			public void onSuccess(List<GWTComponent> result) {
				ReBindComponentPanel(recCodeContainer, result, false);
			}
		});
		
		sendChannelStore.addStoreListener(new StoreListener<GWTChannel>(){
		public void handleEvent(StoreEvent<GWTChannel> e) {
			super.handleEvent(e);
		}
		
		@Override
		public void storeAdd(StoreEvent<GWTChannel> se) {
			super.storeAdd(se);
			List<GWTChannel> models = se.getModels();
			if(models.size() > 0){
				GWTChannel channel = models.get(0);
				GWTAdapter adapter = channel.get(GWTChannel.N_Adapter);
				if(adapter != null){
					int dbValue = adapter.<Integer>get(GWTAdapter.N_CsType);
					if(dbValue == CsType.Client.getDbValue()){
						defChannelStore.add(channel);
						boolean isDefault = channel.<Boolean>get(GWTChannel.N_IsSysDefault);
						if(isDefault)
							defChannel.setValue(channel);
						if(defChannel.getValue() == null && defChannelStore.getModels().size() > 0){
							defChannel.setValue(defChannelStore.getModels().get(0));
						}
					}
				}
			}
		}
		
		@Override
		public void storeRemove(StoreEvent<GWTChannel> se) {
			super.storeRemove(se);
			GWTChannel channel = se.getModel();
			GWTAdapter adapter = channel.get(GWTChannel.N_Adapter);
			if(adapter != null){
				int dbValue = adapter.<Integer>get(GWTAdapter.N_CsType);
				if(dbValue == CsType.Client.getDbValue()){
					if(defChannel.getValue() != null && 
						defChannel.getValue().equals(channel) && 
						defChannelStore.getModels().size() > 0){
						defChannel.setValue(defChannelStore.getModels().get(0));
					}
					defChannelStore.remove(channel);
					if(defChannelStore.getModels().size() == 0)
						defChannel.setValue(null);
				}
			}
		}
		
		@Override
		public void storeUpdate(StoreEvent<GWTChannel> se) {
			super.storeUpdate(se);
			GWTChannel channel = se.getModel();
			GWTAdapter adapter = channel.get(GWTChannel.N_Adapter);
			if(adapter != null){
				int dbValue = adapter.<Integer>get(GWTAdapter.N_CsType);
				if(dbValue == CsType.Client.getDbValue()){
					if(defChannel.getValue() != null && 
							defChannel.getValue().equals(channel))
						defChannel.setValue(channel);
					defChannelStore.update(channel);
				}
			}
		}
	});
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		this.setLayout(new BorderLayout());
		
		service.GetChannelListBySystemId(this.GetSystemID(), new AsyncCallback<List<GWTChannel>>(){

			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert("提示", "通道数据加载失败，请与管理员联系", null);
				
			}

			@Override
			public void onSuccess(List<GWTChannel> result) {
				for(GWTChannel channel : result){
					GWTAdapter adapter = channel.get(GWTChannel.N_Adapter);
					int csType = adapter.<Integer>get(GWTAdapter.N_CsType);
					if(csType == CsType.Client.getDbValue())
						sendChannelStore.add(channel);
					else
						receChannelStore.add(channel);
				}
			}
			
		});
		
		final ContentPanel leftPanel = new ContentPanel();
		leftPanel.setHeaderVisible(false);
		leftPanel.setBorders(false);
		leftPanel.setBodyBorder(false);
		leftPanel.setLayout(new AccordionLayout());
		
		clientAdapterContainer = GetInitComponentPanel(new GWTAdapter(), true);
	    leftPanel.add(clientAdapterContainer);
	    
	    serverAdapterContainer = GetInitComponentPanel(new GWTAdapter(), false);
	    leftPanel.add(serverAdapterContainer);
	    
	    recCodeContainer = GetInitComponentPanel(new GWTRecCode(), false);
	    leftPanel.add(recCodeContainer);
	    
	    packContainer = GetInitComponentPanel(new GWTMsgType(), true);
	    leftPanel.add(packContainer);
	    
	    unpackContainer = GetInitComponentPanel(new GWTMsgType(), false);
	    leftPanel.add(unpackContainer);
		
		BorderLayoutData leftLayoutData = new BorderLayoutData(LayoutRegion.WEST);
		leftLayoutData.setMargins(new Margins(0, 5, 0, 0));
		
		mainPanel = CreateCenterPanel();
		
		BorderLayoutData centerLayoutData = new BorderLayoutData(LayoutRegion.CENTER);
		centerLayoutData.setMargins(new Margins(0, 0, 0, 0));
		
		this.add(leftPanel,  leftLayoutData);
		this.add(mainPanel,  centerLayoutData);
	}
	
	private Button GetComponentButton(final GWTComponent comp){
		
		//组件图标
		AbstractImagePrototype icon = null;
		//组件显示名称
		String text = "";
		//拖拽组分类
		String dragGroup = "";
		
		if(comp instanceof GWTAdapter){
			icon = MainPage.ICONS.RightField();
			text = comp.get(GWTAdapter.N_Protocol) + "适配器";
			if(comp.get(GWTAdapter.N_CsType).equals(CsType.Client.getDbValue()))
				dragGroup = "clientAdapter";
			else
				dragGroup = "serverAdapter";
		}else if(comp instanceof GWTMsgType){
			icon = MainPage.ICONS.RightField();
			text = comp.get(GWTMsgType.N_StyleName);
			dragGroup = "channel";
		}else if(comp instanceof GWTRecCode){
			icon = MainPage.ICONS.RightField();
			text = comp.get(GWTRecCode.N_Type);
			dragGroup = "channel";
		}
		
		final Button btn = new Button();
		btn.setId(String.valueOf(comp.get(GWTComponent.N_ComponentId)));
		btn.setIconAlign(IconAlign.TOP);
		btn.setIcon(icon);
		btn.setText(text);
		btn.setScale(ButtonScale.SMALL);
		btn.setWidth(120);
		
		DragSource source = new DragSource(btn) {
	        @Override
	        protected void onDragStart(DNDEvent event) {
	        	event.setData(comp);
	            event.getStatus().update(El.fly(btn.getElement()).cloneNode(true));
	        }
	      };
	    source.setGroup(dragGroup);
		return btn;
	}
	
	private ContentPanel GetInitComponentPanel(GWTComponent comp, boolean type){
		
		AbstractImagePrototype icon = null;
		String title = "";
		
		if(comp instanceof GWTAdapter){
			icon = MainPage.ICONS.Adapter();
			if(type)
				title = "发起端适配器";
			else
				title = "接收端适配器";
		}else if(comp instanceof GWTMsgType){
			icon = MainPage.ICONS.PackStruct();
			if(type)
				title = "组包组件";
			else
				title = "拆包组件";
		}else if(comp instanceof GWTRecCode){
			icon = MainPage.ICONS.RecCode();
			title= "交易识别";
		}
		
		ContentPanel cp = new ContentPanel();
		cp.setHeading(title);
		cp.setIcon(icon);
		cp.setLayout(new FitLayout());
		cp.setScrollMode(Scroll.AUTOY);
		cp.add(new HtmlContainer());
		
		return cp;
	}
	
	private void ReBindComponentPanel(ContentPanel cp, List<GWTComponent> compList, boolean type){
		
		HtmlContainer html = (HtmlContainer)cp.getItem(0);
		if(html == null)return;
		if(compList.size() == 0)return;
		
		GWTComponent comp = compList.get(0);
		
		List<GWTComponent> tempList = new ArrayList<GWTComponent>();
		String flag = "";
		
		if(comp instanceof GWTAdapter){
			for(GWTComponent c : compList){
				if(type && c.get(GWTAdapter.N_CsType).equals(CsType.Client.getDbValue())){
					tempList.add(c);
				}
				if(!type && c.get(GWTAdapter.N_CsType).equals(CsType.Server.getDbValue())){
					tempList.add(c);
				}
			}
			flag = "A";
		}else if(comp instanceof GWTMsgType){
			for(GWTComponent c : compList){
				if(type && c.get(GWTMsgType.N_Type).equals(MsgType.Pack.getDbValue())){
					tempList.add(c);
				}
				if(!type && c.get(GWTMsgType.N_Type).equals(MsgType.UnPack.getDbValue())){
					tempList.add(c);
				}
			}
			flag = "M";
		}else if(comp instanceof GWTRecCode){
			for(GWTComponent c : compList)
				tempList.add(c);
			flag = "R";
		}
			
		html.setHtml(GetComponentTemplate(tempList, flag));
		for(GWTComponent c : tempList){
			Button btn = GetComponentButton(c);
			html.add(btn, "#" + flag + c.get(GWTComponent.N_ComponentId));
		}
	}
	
	private String GetComponentTemplate(List<GWTComponent> compList, String flag){
		
		StringBuilder sb = new StringBuilder();
		sb.append("<table width='100%' style='text-align:center'>");
		
		for(GWTComponent comp : compList){
			sb.append("<tr height='70px'><td id='" + flag + comp.get(GWTComponent.N_ComponentId) + "'><td/></tr>");
		}
		
		sb.append("</table>");
		
		return sb.toString();
	}
	
	private ContentPanel CreateCenterPanel(){
		
		ContentPanel northPanel = new ContentPanel();
		northPanel.setHeaderVisible(false);
		northPanel.setBorders(false);
		northPanel.setBodyBorder(true);
		northPanel.setLayout(new FitLayout());
		
		StringBuilder sb = new StringBuilder();
		sb.append("<table border='0' width='100%' height='100%'>"
				+"<tr/>"
				+"<tr><td class='tes-table-header'><img Style='vertical-align:middle' src='gxt/images/cus/apply.png' />&nbsp;&nbsp;" + this.GetSystemName() + "</td>" +
						"<td><div style='float:right' id='tdDefaultChannel'></div><div style='float:right; font-size:10pt; vertical-align:middle'>&nbsp;&nbsp;默认发送通道：</div></td></tr>"
				//+"<tr></tr>"
				+"</table>");
		
		defChannel = new ComboBox<GWTChannel>();
		defChannel.setEditable(false);
		defChannel.setDisplayField(GWTChannel.N_ChannelName);
		defChannel.setValueField(GWTChannel.N_ChannelId);
		defChannel.setStore(defChannelStore);

		HtmlContainer titlePanel = new HtmlContainer();
		titlePanel.setHtml(sb.toString());
		titlePanel.add(defChannel, "#tdDefaultChannel");
		
		northPanel.add(titlePanel);
		
		final ContentPanel centerPanel = new ContentPanel();
		centerPanel.setBorders(false);
		centerPanel.setBodyBorder(false);
		centerPanel.setHeaderVisible(false);
		
		ContentPanel clientAdapter = new ContentPanel();
		clientAdapter.setHeaderVisible(false);
		clientAdapter.setBorders(false);
		clientAdapter.setBodyBorder(true);
		//clientAdapter.setHeight("");
		clientAdapter.setWidth("49%");
		clientAdapter.setLayout(new FillLayout());
		clientAdapter.add(GetAdapterGrid(true));
		
		ContentPanel serverAdapter = new ContentPanel();
		serverAdapter.setHeaderVisible(false);
		serverAdapter.setBorders(false);
		serverAdapter.setBodyBorder(true);
		//serverAdapter.setHeight(420);
		serverAdapter.setWidth("49%");
		serverAdapter.setLayout(new FillLayout());
		serverAdapter.add(GetAdapterGrid(false));
		
		ContentPanel splitPanel = new ContentPanel();
		splitPanel.setHeaderVisible(false);
		splitPanel.setBorders(false);
		splitPanel.setBodyBorder(false);
		splitPanel.setWidth("0.5%");
		
		HBoxLayout hLayout = new HBoxLayout();
		hLayout.setHBoxLayoutAlign(HBoxLayoutAlign.STRETCH);
		hLayout.setPadding(new Padding(5));
		centerPanel.setLayout(hLayout);
		centerPanel.add(clientAdapter);
		centerPanel.add(splitPanel);
		centerPanel.add(serverAdapter);
		
		Button btnSave = new Button("保存");
		btnSave.setIcon(MainPage.ICONS.Save());
		btnSave.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				List<GWTChannel> clientList = clientAdapterGrid.getStore().getModels();
				List<GWTChannel> serverList = serverAdapterGrid.getStore().getModels();
				for(GWTChannel channel : serverList)
					clientList.add(channel);
				
					mainPanel.mask("正在保存通道信息请稍后...");
					GWTChannel c = defChannel.getValue();
					String defChannelName = c == null ? "" : c.get(GWTChannel.N_ChannelName).toString();
					GetSysInfo().set(GWTSimuSystem.N_Channel, defChannelName);
					service.SaveChannelList(currSysId, clientList, defChannelName, new AsyncCallback<Void>(){

						@Override
						public void onFailure(Throwable caught) {
							mainPanel.unmask();
							MessageBox.alert("提示", "系统配置保存失败，请与管理员联系", null);
						}

						@Override
						public void onSuccess(Void result) {
							mainPanel.unmask();
							MessageBox.confirm("提示", "系统配置保存成功，是否要部署当前虚拟系统？", new Listener<MessageBoxEvent>(){

								@Override
								public void handleEvent(MessageBoxEvent be) {
									Button msgBtn = be.getButtonClicked();
									if (msgBtn.getText().equalsIgnoreCase("Yes")){
										DeploySystem();
									}
								}
							});
						}
					});
			}
			
		});
		
		Button btnReset = new Button("重置");
		btnReset.setIcon(MainPage.ICONS.menuTreeLeaf());
		Button btnDeploy = new Button("部署");
		btnDeploy.setIcon(MainPage.ICONS.Deploy());
		btnDeploy.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				DeploySystem();
			}
		});
		lablePath = new LabelField();
		lablePath.setFieldLabel("虚拟系统部署路径");
		lablePath.setValue("");
		
		ToolBar tb = new ToolBar();
		tb.add(btnSave);
		//tb.add(btnReset);
		tb.add(new SeparatorToolItem());
		tb.add(btnDeploy);
		tb.add(lablePath);
		
		ContentPanel cp = new ContentPanel();
		cp.setHeaderVisible(false);
		cp.setLayout(new BorderLayout());
		cp.setBodyBorder(true);
		cp.add(northPanel, new BorderLayoutData(LayoutRegion.NORTH, 50));
		cp.add(centerPanel, new BorderLayoutData(LayoutRegion.CENTER));
		cp.setBottomComponent(tb);
		
		return cp;
	}
	
	private ContentPanel GetAdapterGrid(final boolean isClient){
		
		GridCellRenderer<GWTChannel> nameRender = new GridCellRenderer<GWTChannel>(){

			@Override
			public Object render(final GWTChannel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GWTChannel> store, final Grid<GWTChannel> grid) {
				String template = "<span>" + model.get(GWTChannel.N_ChannelName) + "</span>"
								+"<br/><br/><span>" + model.get(GWTChannel.N_IP)
								+":" + model.get(GWTChannel.N_Port)
								+ "</span>";
				return template;
			}
			
		};
		
		GridCellRenderer<GWTChannel> channelRender = new GridCellRenderer<GWTChannel>(){

			@Override
			public Object render(final GWTChannel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GWTChannel> store, final Grid<GWTChannel> grid) {
				ContentPanel tempCp = GetChannelGridCellPanel(model, String.valueOf(rowIndex), isClient);
				return tempCp;
			}
			
		};
		
		ColumnConfig name = new ColumnConfig(GWTChannel.N_ChannelName, "通道名", 50);
		if(isClient)
			name.setRenderer(nameRender);
		ColumnConfig channel = new ColumnConfig(GWTChannel.N_ChannelId, "组件配置", 170);
		channel.setRenderer(channelRender);

	    ColumnModel cm = new ColumnModel(Arrays.asList(name, channel));

	    final Grid<GWTChannel> grid = isClient ?
	    		new Grid<GWTChannel>(sendChannelStore, cm) :
	    		new Grid<GWTChannel>(receChannelStore, cm) ;
	    grid.setBorders(false);
	    grid.setStripeRows(true);
	    grid.setHeight("100%");
	    grid.setAutoExpandColumn(GWTChannel.N_ChannelId);
	    grid.setTrackMouseOver(false);
	    grid.getView().setForceFit(true);
	    
	    Button btnEdit = new Button("编辑");
	    btnEdit.setIcon(MainPage.ICONS.EditCom());
	    btnEdit.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				GWTChannel selectChannel = grid.getSelectionModel().getSelectedItem();
				if(selectChannel != null){
					ShowChannelWindow(selectChannel, isClient, grid.getStore());
				}
			}
	    	
	    });
	    Button btnDel = new Button("删除");
	    btnDel.setIcon(MainPage.ICONS.DelCom());
	    btnDel.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				GWTChannel selectChannel = grid.getSelectionModel().getSelectedItem();
				if(selectChannel != null)
					grid.getStore().remove(selectChannel);
			}
	    	
	    });
	    
	    ToolBar tb = new ToolBar();
	    tb.add(btnEdit);
	    tb.add(btnDel);
	    
	    ContentPanel cp = new ContentPanel();
	    if(isClient)
	    	cp.setHeading("发起端通道");
	    else
	    	cp.setHeading("接收端通道");
	    cp.setBorders(false);
	    cp.setBodyBorder(false);
	    cp.setLayout(new FillLayout());
	    //
	    cp.setBottomComponent(tb);
	    cp.add(grid);
	    
	    final String channelName = isClient ? "SendChannel" : "ReceiveChannel";
	    
	    //new GridDragSource(grid);
		DropTarget target = new DropTarget(grid) {
		      @Override
		      protected void onDragDrop(DNDEvent event) {
		        super.onDragDrop(event);
		        GWTAdapter src = (GWTAdapter)event.getData();
		        GWTChannel channel = new GWTChannel();
		        int index = grid.getStore().getCount() + 1;
		        channel.set(GWTChannel.N_ChannelName, channelName + String.valueOf(index));
		        channel.set(GWTChannel.N_IP, "127.0.0.1");
		        channel.set(GWTChannel.N_Port, "888" + String.valueOf(index));
		        channel.set(GWTChannel.N_Adapter, src);
		        channel.set(GWTChannel.N_AdapterConfig, src.get(GWTAdapter.N_ConfigTemplate));
		        grid.getStore().add(channel);
		      }
		};
		if(isClient){
			target.setGroup("clientAdapter");
			clientAdapterGrid = grid;
		}
		else{
			target.setGroup("serverAdapter");
			serverAdapterGrid = grid;
		}
	    
	    return cp;
	}
	
	private ContentPanel GetChannelGridCellPanel(final GWTChannel channel, String id, boolean isClient){
		
		ContentPanel cp = new ContentPanel();
		cp.setHeaderVisible(false);
		cp.setBodyBorder(false);
		cp.setBorders(false);
		cp.setWidth("100%");
		cp.setHeight(72);
		
		String adapterTdID = "tdAdapter";
		String packTdID = "tdPackComp";
		String unpackTdID = "tdUnPackComp";
		String recCodeTdID = "tdRecCodeComp";
		
		if(isClient){
			adapterTdID += "Send" + id;
			packTdID += "Send" + id;
			unpackTdID += "Send" + id;
			recCodeTdID += "Send" + id;
		}else{
			adapterTdID += "Receive" + id;
			packTdID += "Receive" + id;
			unpackTdID += "Receive" + id;
			recCodeTdID += "Receive" + id;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("<table width='100%' height='100%' border='0'>"
				+"<tr><td width='18%' Style='font-size:9pt; border:solid 1 #0000cc'>通讯协议</td><td width='32%' id='" + adapterTdID + "' Style='border:solid 1 #0000cc'></td>"
				+"<td width='18%' Style='font-size:9pt; border:solid 1 #993399'>组包组件</td><td width='32%' id='" + packTdID + "' Style='border:solid 1 #993399'></td></tr>"
				+"<tr><td width='18%' Style='font-size:9pt; border:solid 1 #009933'>交易识别</td><td width='32%' id='" + recCodeTdID + "' Style='border:solid 1 #009933'></td>"
				+"<td width='18%' Style='font-size:9pt; border:solid 1 #993399'>拆包组件</td><td width='32%' id='" + unpackTdID + "' Style='border:solid 1 #993399'></td></tr>"
				+"</table>");
		
		final HtmlContainer channelContainer = new HtmlContainer();
		channelContainer.setHtml(sb.toString());
		
		//Adapter button
		GWTAdapter adapter = channel.get(GWTChannel.N_Adapter);
		Button adapterBtn = new Button();
		//adapterBtn.setId("adapter");
		if(adapter != null){
			adapterBtn.setText(adapter.get(GWTAdapter.N_Protocol).toString());
			adapterBtn.setIcon(MainPage.ICONS.Adapter());
		}
		else{
			adapterBtn.setText("未配置");
			adapterBtn.setIcon(MainPage.ICONS.CaseOracleData());
		}
		//adapterBtn.setIconAlign(IconAlign.LEFT);
		adapterBtn.setScale(ButtonScale.SMALL);
		adapterBtn.setWidth("100%");
		adapterBtn.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				if(channel.get(GWTChannel.N_Adapter) != null){
					ShowConfigWindow(ComponentEnum.Adapter, channel);
				}
			}
		});
		
		GWTMsgType pack = channel.get(GWTChannel.N_Pack);
		Button packCompBtn = new Button();
		packCompBtn.setId("pack");
		if(pack != null){
			String text = pack.get(GWTMsgType.N_StyleName);
	    	if(text.length() > 8)
	    		text = text.substring(0, 6) + "...";
			packCompBtn.setText(text);
			packCompBtn.setIcon(MainPage.ICONS.PackStruct());
		}else{
			packCompBtn.setText("未配置");
			packCompBtn.setIcon(MainPage.ICONS.CaseOracleData());
		}
		//packCompBtn.setIconAlign(IconAlign.LEFT);
		packCompBtn.setScale(ButtonScale.SMALL);
		packCompBtn.setWidth("100%");
		packCompBtn.addSelectionListener(new SelectionListener<ButtonEvent>(){
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				if(channel.get(GWTChannel.N_Pack) != null){
					GWTComponent pack = channel.get(GWTChannel.N_Pack);
					ShowMsgTypeConfigWindow(pack);
				}
					
			}
		});
		
		GWTMsgType unpack = channel.get(GWTChannel.N_UnPack);
		Button unpackCompBtn = new Button();
		unpackCompBtn.setId("unpack");
		if(unpack != null){
			String text = unpack.get(GWTMsgType.N_StyleName);
	    	if(text.length() > 8)
	    		text = text.substring(0, 6) + "...";
			unpackCompBtn.setText(text);
			unpackCompBtn.setIcon(MainPage.ICONS.PackStruct());
		}else{
			unpackCompBtn.setText("未配置");
			unpackCompBtn.setIcon(MainPage.ICONS.CaseOracleData());
		}
		//unpackCompBtn.setIconAlign(IconAlign.LEFT);
		unpackCompBtn.setScale(ButtonScale.SMALL);
		unpackCompBtn.setWidth("100%");
		unpackCompBtn.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				if(channel.get(GWTChannel.N_UnPack) != null){
					GWTComponent unpack = channel.get(GWTChannel.N_UnPack);
					ShowMsgTypeConfigWindow(unpack);
				}
					
			}
			
		});
		
		GWTRecCode code = channel.get(GWTChannel.N_TransRecognizer);
		Button recCodeCompBtn = new Button();
		recCodeCompBtn.setId("recCode");
		if(code != null){
			String text = code.get(GWTRecCode.N_Name);
	    	if(text.length() > 8)
	    		text = text.substring(0, 6) + "...";
	    	recCodeCompBtn.setText(text);
	    	recCodeCompBtn.setIcon(MainPage.ICONS.RecCode());
		}else{
			recCodeCompBtn.setText("未配置");
			recCodeCompBtn.setIcon(MainPage.ICONS.CaseOracleData());
		}
		//recCodeCompBtn.setIconAlign(IconAlign.LEFT);
		recCodeCompBtn.setScale(ButtonScale.SMALL);
		recCodeCompBtn.setWidth("100%");
		recCodeCompBtn.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				if(channel.get(GWTChannel.N_TransRecognizer) != null){
					ShowConfigWindow(ComponentEnum.RecCode, channel);
				}
			}
		});
		
		channelContainer.add(adapterBtn, "#" + adapterTdID);
		channelContainer.add(packCompBtn, "#" + packTdID);
		channelContainer.add(unpackCompBtn, "#" + unpackTdID);
		channelContainer.add(recCodeCompBtn, "#" + recCodeTdID);
		//channelContainer.setHeight("50%");
		
		DropTarget target = new DropTarget(channelContainer) {
		      @Override
		      protected void onDragDrop(DNDEvent event) {
		        //super.onDragDrop(event);
		    	GWTComponent src = (GWTComponent)event.getData();
		    	Button btn = null;
		        if(src instanceof GWTMsgType){
		        	String text = src.get(GWTMsgType.N_StyleName);
		        	if(text.length() > 8)
		        		text = text.substring(0, 6) + "...";
		        	if(src.get(GWTMsgType.N_Type).equals(MsgType.Pack.getDbValue())){
		        		btn = (Button)channelContainer.getItemByItemId("pack");
		        		channel.set(GWTChannel.N_Pack, src);
		        	}else{
		        		btn = (Button)channelContainer.getItemByItemId("unpack");
		        		channel.set(GWTChannel.N_UnPack, src);
		        	}
			        btn.setText(text);
			        btn.setIcon(MainPage.ICONS.PackStruct());
		        }else if(src instanceof GWTRecCode){
		        	String text = src.get(GWTRecCode.N_Type);
		        	if(text.length() > 8)
		        		text = text.substring(0, 6) + "...";
		        	btn = (Button)channelContainer.getItemByItemId("recCode");
			        btn.setText(text);
			        btn.setIcon(MainPage.ICONS.search());
			        channel.set(GWTChannel.N_TransRecognizer, src);
			        String conf = src.get(GWTRecCode.N_Template);
			        channel.set(GWTChannel.N_RecognizerCfgInfo, conf);
		        }else
		        	return;
		      }
		};
		target.setGroup("channel");
		
		cp.setLayout(new FillLayout());
		cp.add(channelContainer);
		return cp;
	}
	
	private void ShowChannelWindow(final GWTChannel channel, boolean isClient, final ListStore<GWTChannel> store){
		final Window window = new Window();
		
		window.setHeading("编辑通道");
		window.setSize(360, 160);
		window.setPlain(true);
		window.setModal(true);
		window.setBlinkModal(false);
		window.setLayout(new FitLayout());
		
		final FormPanel formPanel = new FormPanel();
		formPanel.setBorders(false);
		formPanel.setBodyBorder(false);
		formPanel.setLabelWidth(55);
		formPanel.setPadding(5);
		formPanel.setHeaderVisible(false);

		String labelStyle = "width:60px;";
		FormData formdata = new FormData("95%");
		
		final RequireTextField tfChannelName = new RequireTextField("通道名称");
		tfChannelName.setLabelStyle(labelStyle);
		tfChannelName.setMaxLength(32);
		tfChannelName.setValue(channel.get(GWTChannel.N_ChannelName).toString());
		formPanel.add(tfChannelName, formdata);
		
		final RequireTextField tfIp = new RequireTextField("Ip地址");
		tfIp.setLabelStyle(labelStyle);
		tfIp.setFieldLabel("Ip地址");
		tfIp.setValue(channel.get(GWTChannel.N_IP).toString());
		if(isClient)
			formPanel.add(tfIp, formdata);
		
		final RequireTextField tfPort = new RequireTextField("端口");
		tfPort.setLabelStyle(labelStyle);
		tfPort.setFieldLabel("端口");
		tfPort.setValue(channel.get(GWTChannel.N_Port).toString());
		if(isClient)
			formPanel.add(tfPort, formdata);
		
		window.addButton(new Button("确定", new SelectionListener<ButtonEvent>() {
			@SuppressWarnings("deprecation")
			public void componentSelected(ButtonEvent ce) {
				if(formPanel.isValid()){
					channel.set(GWTChannel.N_ChannelName, tfChannelName.getValue());
					channel.set(GWTChannel.N_IP, tfIp.getValue());
					channel.set(GWTChannel.N_Port, tfPort.getValue());
					store.update(channel);
					window.close();
				}
			}
		}));
		
		window.addButton(new Button("取消", new SelectionListener<ButtonEvent>() {
			@SuppressWarnings("deprecation")
			public void componentSelected(ButtonEvent ce) {
				window.close();
			}
		}));
		window.add(formPanel);
		window.show();
	}

	private void ShowMsgTypeConfigWindow(GWTComponent edit) {
		final Window window = new Window();
		
		if(edit.get(GWTMsgType.N_Type).equals(MsgType.Pack.getDbValue()))
			window.setHeading("组包组件配置");
		else
			window.setHeading("拆包组件配置");
		window.setSize(500, 550);
		window.setPlain(true);
		window.setModal(true);
		window.setBlinkModal(false);
		window.setLayout(new FitLayout());

		final FormPanel formPanel = new FormPanel();
		formPanel.setBorders(false);
		formPanel.setBodyBorder(false);
		formPanel.setLabelWidth(55);
		formPanel.setPadding(5);
		formPanel.setHeaderVisible(false);

		String labelStyle = "width:80px;";
		FormData formdata = new FormData("95%");
		
		final RequireTextField tfStyleName = new RequireTextField("报文样式名称");
		tfStyleName.setLabelStyle(labelStyle);
		tfStyleName.setMaxLength(32);
		tfStyleName.setReadOnly(true);
		formPanel.add(tfStyleName, formdata);
		
		final SimpleComboBox<String> typeCB = new SimpleComboBox<String>();
		typeCB.setEditable(false);
		typeCB.setFieldLabel("报文类型");
		typeCB.setLabelStyle(labelStyle);
		for(MsgType t : MsgType.values()){
			typeCB.add(t.getChDesc());
		}
		typeCB.setSimpleValue(MsgType.values()[0].getChDesc());
		typeCB.setReadOnly(true);
		formPanel.add(typeCB, formdata);
		
		final SimpleComboBox<String> msgType = new SimpleComboBox<String>();
		msgType.setFieldLabel("报文协议");
		msgType.setLabelStyle(labelStyle);
		msgType.add(Arrays.asList("XML", "8583", "定长", "二进制", "其它"));
		msgType.setSimpleValue("XML");
		msgType.setReadOnly(true);
		formPanel.add(msgType, formdata);
		
		final RequireTextField tfClass = new RequireTextField("组件类");
		tfClass.setLabelStyle(labelStyle);
		tfClass.setFieldLabel("组件类");
		tfClass.setReadOnly(true);
		formPanel.add(tfClass, formdata);

		final TextArea tfConfTemplate = new TextArea();
		tfConfTemplate.setLabelStyle(labelStyle);
		tfConfTemplate.setHeight(360);
		//tfConfTemplate.setMaxLength(1280);
		tfConfTemplate.setFieldLabel("样式文件内容");
		formPanel.add(tfConfTemplate, formdata);
		
		if(edit != null){
			tfStyleName.setValue(edit.get(GWTMsgType.N_StyleName).toString());
			//tfDesc.setValue(edit.get(GWTMsgType.N_Desc).toString());
			int type = edit.<Integer>get(GWTMsgType.N_Type);
			typeCB.setSimpleValue(MsgType.valueOfDbValue(type).getChDesc());
			msgType.setSimpleValue(edit.get(GWTMsgType.N_Protocol).toString());
			tfClass.setValue(edit.get(GWTMsgType.N_Class).toString());
			tfConfTemplate.setValue(edit.get(GWTMsgType.N_Content).toString());
		}
		
		window.addButton(new Button("关闭", new SelectionListener<ButtonEvent>() {
			@SuppressWarnings("deprecation")
			public void componentSelected(ButtonEvent ce) {
				window.close();
			}
		}));
		window.add(formPanel);
		window.show();
	}
	
	private void ShowConfigWindow(final ComponentEnum type, final GWTChannel channel){
		final Window win = new Window();
		win.setSize(600, 500);
		win.setPlain(true);
		win.setModal(true);
		win.setLayout(new FillLayout());
		
		String msg = "";
		if(type == ComponentEnum.Adapter){
			msg = channel.get(GWTChannel.N_AdapterConfig);
			win.setHeading("适配器参数 - " + channel.get(GWTChannel.N_ChannelName));
		}else{
			msg = channel.get(GWTChannel.N_RecognizerCfgInfo);
			win.setHeading("交易识别参数 - " + channel.get(GWTChannel.N_ChannelName));
		}
		
		final TextArea ta = new TextArea();
		ta.setValue(msg);
		
		Button btnSave = new Button("确定");
		btnSave.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@SuppressWarnings("deprecation")
			@Override
			public void componentSelected(ButtonEvent ce) {
				if(type == ComponentEnum.Adapter){
					channel.set(GWTChannel.N_AdapterConfig, ta.getValue());
				}
				else{
					channel.set(GWTChannel.N_RecognizerCfgInfo, ta.getValue());
				}
//				if(dbValue == CsType.Client.getDbValue())
//					sendChannelStore.update(channel);
//				else
//					receChannelStore.update(channel);
				win.close();
			}
		});
		
		Button btnCancel = new Button("取消");
		btnCancel.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@SuppressWarnings("deprecation")
			@Override
			public void componentSelected(ButtonEvent ce) {
				win.close();
			}
		});
		
		win.add(ta);
		win.addButton(btnSave);
		win.addButton(btnCancel);
		
		win.show();
	}
	
	private void DeploySystem(){
		lablePath.setValue("");
		mainPanel.mask("正在部署虚拟系统请稍后...");
		GWTChannel c = defChannel.getValue();
		String defChannelName = c == null ? "" : c.get(GWTChannel.N_ChannelName).toString();
		service.DeploySystem(currSysId, defChannelName, new AsyncCallback<String>(){

			@Override
			public void onFailure(Throwable caught) {
				mainPanel.unmask();
				lablePath.setValue("");
				MessageBox.alert("提示", "虚拟系统部署失败，请与管理员联系", null);
			}

			@Override
			public void onSuccess(String result) {
				mainPanel.unmask();
				MessageBox.info("提示", "虚拟系统部署成功!", null);
				result = result.replace('/', '\\').substring(1);
				lablePath.setValue(result);
			}
		});
	}
}
