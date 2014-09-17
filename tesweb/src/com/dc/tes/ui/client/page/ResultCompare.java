package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.List;

import com.dc.tes.ui.client.ICaseService;
import com.dc.tes.ui.client.ICaseServiceAsync;
import com.dc.tes.ui.client.IResultService;
import com.dc.tes.ui.client.IResultServiceAsync;
import com.dc.tes.ui.client.MainPage;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.model.GWTCompareResult;
import com.dc.tes.ui.client.model.GWTMsgAttribute;
import com.dc.tes.ui.client.model.GWTPack_Struct;
import com.dc.tes.ui.client.model.GWTResultLog;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.dc.tes.ui.client.model.GWTTransaction;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 执行案例发起，并弹出结果比对对话框
 * @author scckobe
 *
 */
public class ResultCompare{
	private ICaseServiceAsync caseService = null;
	private int treeWidth = 200;
	TreeGrid<ModelData> tree;
	TreeStore<ModelData> store;
	private String executeLogID = "";
	private IResultServiceAsync resultService = ServiceHelper.GetDynamicService("result", IResultService.class);
	
	public ResultCompare(){
		caseService = ServiceHelper.GetDynamicService(CasePage.SERVERNAME, ICaseService.class);
	}
	
	private boolean HaveIpAndPort(GWTSimuSystem sysInfo)
	{
		try
		{
			String IP = sysInfo.GetIP();
			int port = sysInfo.GetPort();
			if(!IP.isEmpty() && port != 0)
				return true;
		}
		catch (Exception e) {
		}
		MessageBox.alert("友情提示", "所属系统的IP地址或者端口号不符合规定", null);
		return false;
	}
	
	public void Show(final GWTSimuSystem sysInfo,final GWTTransaction tranInfo,final String caseID,final Component panel){
		if(HaveIpAndPort(sysInfo))
		{
			new BasePage();
			panel.mask("案例执行中...");
			caseService.Insert2DataBase(caseID, "Administrator", sysInfo.GetSystemID(), new AsyncCallback<String>() {

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
					MessageBox.alert("友情提示", "新建执行日志记录失败", null);
				}

				@Override
				public void onSuccess(String result) {
					// TODO Auto-generated method stub
					caseService.GetResultCompare(sysInfo,tranInfo,caseID,result,GetCallBack(panel));
				}
				
			});
		}
	}
	
	public void Show(GWTSimuSystem sysInfo,GWTTransaction tranInfo,Component panel){
		if(HaveIpAndPort(sysInfo))
		{
			panel.mask("案例执行中...");
			caseService.GetResultCompare(sysInfo,tranInfo,GetCallBack(panel));
		}
	}
	
	public void Show(GWTSimuSystem sysInfo,GWTTransaction tranInfo,String caseName,GWTPack_Struct root,Component modal){
		if(HaveIpAndPort(sysInfo))
		{
			modal.mask("案例执行中...");
			caseService.GetResultCompare(sysInfo,tranInfo,caseName,root, GetCallBack(modal));
		}
	}
	
	private AsyncCallback<GWTCompareResult> GetCallBack(final Component modal)
	{
		return new AsyncCallback<GWTCompareResult>() {
			@Override
			public void onFailure(Throwable caught) {
				modal.unmask();
				MessageBox.alert("错误提示", caught.getMessage(), null);
			}

			@Override
			public void onSuccess(GWTCompareResult result) {
				
				if(result.getBooleanResult())
					ShowResult(result.getCompareResult(),modal);
				else
				{
					modal.unmask();
					ShowFailWin(result.getErrorMsg());
				}
			}
		};
	}
	
	private void ShowFailWin(String msg)
	{
		MessageBox.alert("提示信息", msg, null);
	}
	
	public void ShowResult(GWTPack_Struct root,final Component modal)
	{
		
		if(root == null)
		{			
//			if(modal != null)
//				modal.mask("执行完成");
//			final Timer time = new Timer() {
//				@Override
//				public void run() {
//					modal.unmask();
//					this.cancel();
//				}
//			};
//			time.schedule(2000);
			modal.unmask();
			MessageBox.confirm("提示", "执行完成，是否立即查看执行结果？", new Listener<MessageBoxEvent>() {
				
				@Override
				public void handleEvent(MessageBoxEvent be) {
					// TODO Auto-generated method stub
					if(be.getButtonClicked().getText().equalsIgnoreCase("yes")){
						resultService.GetResultLog(executeLogID, new AsyncCallback<GWTResultLog>() {

							@Override
							public void onFailure(
									Throwable caught) {
								// TODO Auto-generated method stub
								caught.printStackTrace();														
							}

							@Override
							public void onSuccess(
									GWTResultLog result) {
								// TODO Auto-generated method stub
								if(result == null){
									MessageBox.alert("提示", "未找到本次执行结果，请检查核心是否启动", null);
								}else
									ResultLogPage.openDetailPage(result);														
							}
						});
					}
				}
			});
			return;
		}//if root == null
		if(modal != null)
			modal.unmask();
		
		showCompareWindow(root);
	}
	
	public void showCompareWindow(GWTPack_Struct root) {
		// TODO Auto-generated method stub
		Window resuWindow = new Window();
		resuWindow.setHeading("案例预期结果比对");
		resuWindow.setHeight(520);
		
		List<ModelData> storeRoot = root.getChildren();
		store = new TreeStore<ModelData>();
		store.add(storeRoot, true);
		
		tree = new TreeGrid<ModelData>(store, DefineColumnModel(root.getFieldAttrList()))
		{
			@Override
			protected void afterRenderView()
			{
				super.afterRenderView();
				setExpanded(store.getAt(0), true, true);
			}
		};
		treeWidth = Math.max(500, treeWidth);
		tree.setHeight(500);
		tree.getView().setForceFit(true);
		tree.setBorders(false);
		tree.getStyle().setLeafIcon(MainPage.ICONS.GWTPack_Field());
		tree.setWidth(treeWidth);
		tree.setAutoExpandColumn("name");

		ToolBar lbIcon = new ToolBar();
		lbIcon.add(new LabelField("图示："));
		
		HtmlContainer lbWarn = new HtmlContainer("<div class = \"WarnIcon\">&nbsp;</div>");
		LabelField lbWarnInfo = new LabelField(":预期结果为空，实际值不为空；");
		lbIcon.add(lbWarn);
		lbIcon.add(lbWarnInfo);
		
		HtmlContainer lbError = new HtmlContainer("<div class = \"ErrorIcon\">&nbsp;</div>");
		LabelField lbErrorInfo = new LabelField(":预期值与实际值不相同");
		lbIcon.add(lbError);
		lbIcon.add(lbErrorInfo);
		
//		lbIcon.add
		resuWindow.setTopComponent(lbIcon);
		
		resuWindow.setLayout(new FillLayout());
		resuWindow.add(tree);
		resuWindow.setWidth(treeWidth + 20);
		
		
		resuWindow.show();
	}

	/**
	 * 根据Field树形生成列
	 * 
	 * @param attrs
	 * @return
	 */
	private ColumnModel DefineColumnModel(GWTMsgAttribute[] attrs) {
		List<ColumnConfig> cmList = new ArrayList<ColumnConfig>();
		ColumnConfig conf;
		treeWidth = 0;
		for (GWTMsgAttribute attr : attrs)
		{
			if (((String) attr.get("name")).equals("expect_result")) 
				continue;
			
			String widthStr = attr.get("width").toString();
			int width = 100;
			if (!widthStr.equals(""))
				width = Integer.parseInt(widthStr);
			
			conf = new ColumnConfig((String) attr.get("name"),
					(String) attr.get("display"), width);
			conf.setSortable(false);
			
			if (((String) attr.get("name")).equals("name")) {
				conf.setWidth(200);
				conf.setRenderer(new TreeGridCellRenderer<ModelData>());  
				ColumnConfig data1 = new ColumnConfig("expect_result", "预期值", 150);
				ColumnConfig data2 = new ColumnConfig("data", "实际值", 150);
				data1.setSortable(false);
				data1.setRenderer(new GridCellRenderer<ModelData>()
						{
							public Object render(ModelData model, String property, ColumnData config, int rowIndex,
						      int colIndex, ListStore<ModelData> store, Grid<ModelData> grid) {
									String realValue = model.get("data");
									String preValue = model.get(property);
									String appStr = " title = \"" + preValue + "\">" + preValue + "</div>";
									
									if(realValue == null)
										realValue = "";	
									
									if(preValue == null)
										return "";
									else if(realValue.compareTo(preValue) != 0)
									{
										if(realValue.isEmpty())
											return "<div class = \"ErrorSpan\" "+ appStr;
									}
									return "<div" + appStr;
						}});
				
				data2.setSortable(false);
				
				data2.setRenderer(new GridCellRenderer<ModelData>()
						{
							public Object render(ModelData model, String property, ColumnData config, int rowIndex,
						      int colIndex, ListStore<ModelData> store, Grid<ModelData> grid) {
									String realValue = model.get(property);
									String preValue = model.get("expect_result");
									String appStr = " title = \"" + realValue + "\">" + realValue + "</div>";
									if(preValue == null)
										preValue = "";
									if(realValue == null)
										return "";																			
									else if(realValue.compareTo(preValue) != 0)
									{
										if(preValue.isEmpty())
											return "<div class = \"WarnSpan\" "+ appStr;
										else
											return "<div  class = \"ErrorSpan\" "+ appStr;
									}
									return "<div" + appStr;
						}});

				treeWidth += 300;
				cmList.add(0, conf);
				cmList.add(1,data1);
				cmList.add(2,data2);
				continue;
			}
			else
			{
				cmList.add(conf);
			}
			treeWidth += conf.getWidth();
		}

		return new ColumnModel(cmList);
	}

	public void Show(final GWTSimuSystem sysInfo, String userId, final String transactionID,
			final String caseID, final Component panel) {
		if(HaveIpAndPort(sysInfo))
		{
			panel.mask("案例执行中...");
			caseService.Insert2DataBase(caseID, userId, sysInfo.GetSystemID(), new AsyncCallback<String>() {

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
					MessageBox.alert("友情提示", "新建执行日志记录失败", null);
				}

				@Override
				public void onSuccess(String result) {
					// TODO Auto-generated method stub
					executeLogID = result;
					caseService.GetResultCompare(sysInfo,transactionID,caseID,result,GetCallBack(panel));
				}
				
			});
		}
		
	}
}
