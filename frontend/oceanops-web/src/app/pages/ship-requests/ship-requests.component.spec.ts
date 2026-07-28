import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShipRequestsComponent } from './ship-requests.component';

describe('ShipRequestsComponent', () => {
  let component: ShipRequestsComponent;
  let fixture: ComponentFixture<ShipRequestsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShipRequestsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ShipRequestsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
